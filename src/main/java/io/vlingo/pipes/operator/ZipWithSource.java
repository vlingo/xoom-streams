// Copyright © 2012-2019 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.pipes.operator;

import io.vlingo.actors.Actor;
import io.vlingo.actors.CompletesEventually;
import io.vlingo.actors.Stage;
import io.vlingo.common.Cancellable;
import io.vlingo.common.Completes;
import io.vlingo.common.Scheduled;
import io.vlingo.common.Tuple2;
import io.vlingo.pipes.Record;
import io.vlingo.pipes.Source;
import io.vlingo.pipes.Stream;
import io.vlingo.pipes.actor.ComposedMaterialized;
import io.vlingo.pipes.actor.Materialized;
import io.vlingo.pipes.actor.MaterializedSource;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

public class ZipWithSource<L, R> extends BasicOperator<Tuple2<L, R>, Tuple2<L, R>> {
    private final Source<R> right;
    private final Queue<Record<Tuple2<L, R>>> queue;

    public ZipWithSource(Source<R> right, Queue<Record<Tuple2<L, R>>> queue) {
        this.right = right;
        this.queue = queue;
    }

    @Override
    public void whenValue(Record<Tuple2<L, R>> value) {
        queue.add(value);
    }

    @Override
    public CompletableFuture<Record<Tuple2<L, R>>[]> poll() {
        CompletableFuture<Record<Tuple2<L, R>>[]> result = CompletableFuture.completedFuture(queue.toArray(new Record[0]));
        queue.clear();

        return result;
    }

    @Override
    public Materialized materialize(Stage stage, MaterializedSource source) {
        MaterializedSource rightMaterializedSource = right.materialize(stage, null).asSource().await();
        MaterializedSource selfSource = stage.actorFor(MaterializedSource.class, ZipWithSourceMaterializedActor.class, source, rightMaterializedSource, Stream.DEFAULT_POLL_INTERVAL);

        return stage.actorFor(Materialized.class, ComposedMaterialized.class, Arrays.asList(selfSource, rightMaterializedSource));
    }

    public static class ZipWithSourceMaterializedActor extends Actor implements MaterializedSource, Scheduled<Void> {
        private final int MAX_BUFFER = 32;

        private final MaterializedSource leftSource;
        private final MaterializedSource rightSource;
        private final Queue<Record> pendingLeft;
        private final Queue<Record> pendingRight;
        private final int pollInterval;
        private final Cancellable cancellable;
        private final MaterializedSource selfAsMaterializedSource;

        public ZipWithSourceMaterializedActor(MaterializedSource leftSource, MaterializedSource rightSource, int pollInterval) {
            this.leftSource = leftSource;
            this.rightSource = rightSource;
            this.pendingLeft = new ArrayDeque<>(32);
            this.pendingRight = new ArrayDeque<>(32);
            this.pollInterval = pollInterval;
            this.selfAsMaterializedSource = selfAs(MaterializedSource.class);
            this.cancellable = scheduler().schedule(selfAs(Scheduled.class), null, 0, pollInterval);
        }

        @Override
        public Completes<Optional<Record[]>> nextIfAny() {
            CompletesEventually completes = completesEventually();

            if (pendingLeft.isEmpty() || pendingRight.isEmpty()) {
                completes.with(Optional.empty());
            } else {
                int recordNumber = Math.min(pendingLeft.size(), pendingRight.size());
                Record[] records = new Record[recordNumber];
                for (int i = 0; i < recordNumber; i++) {
                    Record left = pendingLeft.poll();
                    Record right = pendingRight.poll();

                    records[i] = left.mergeLeft(right);
                }

                completes.with(Optional.of(records));
            }

            return completes();
        }

        @Override
        public Completes<MaterializedSource> asSource() {
            return completes().with(selfAsMaterializedSource);
        }

        @Override
        public void intervalSignal(Scheduled<Void> scheduled, Void data) {
            if (pendingLeft.size() < MAX_BUFFER) {
                Completes<Optional<Record[]>> leftValues = leftSource.nextIfAny();
                leftValues.andFinallyConsume(opt -> opt.ifPresent(records -> pendingLeft.addAll(Arrays.asList(records))));
            }

            if (pendingRight.size() < MAX_BUFFER) {
                Completes<Optional<Record[]>> rightValues = rightSource.nextIfAny();
                rightValues.andFinallyConsume(opt -> opt.ifPresent(records -> pendingRight.addAll(Arrays.asList(records))));
            }
        }

        @Override
        public void stop() {
            this.cancellable.cancel();
            super.stop();
        }
    }
}
