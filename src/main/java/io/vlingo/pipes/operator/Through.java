// Copyright © 2012-2019 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.pipes.operator;

import io.vlingo.actors.Actor;
import io.vlingo.actors.Stage;
import io.vlingo.common.Cancellable;
import io.vlingo.common.Completes;
import io.vlingo.common.Scheduled;
import io.vlingo.pipes.Record;
import io.vlingo.pipes.Sink;
import io.vlingo.pipes.Stream;
import io.vlingo.pipes.actor.ComposedMaterialized;
import io.vlingo.pipes.actor.Materialized;
import io.vlingo.pipes.actor.MaterializedSource;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

public class Through<T> extends BasicOperator<T, T> {
    private final Sink<T> wiretap;
    private final Queue<Record<T>> queue;

    public Through(Sink<T> wireTap, Queue<Record<T>> queue) {
        this.wiretap = wireTap;
        this.queue = queue;
    }

    @Override
    public void whenValue(Record<T> value) {
        queue.add(value);
    }

    @Override
    public CompletableFuture<Record<T>[]> poll() {
        CompletableFuture<Record<T>[]> result = CompletableFuture.completedFuture(queue.toArray(new Record[0]));
        queue.clear();

        return result;
    }

    @Override
    public Materialized materialize(Stage stage, MaterializedSource source) {
        PushBasedSink pushBasedSink = stage.actorFor(PushBasedSink.class, PushBasedTemporalMaterializedSink.class);
        Materialized sink = wiretap.materialize(stage, (MaterializedSource) pushBasedSink.asSource().await());

        MaterializedSource selfSource = stage.actorFor(MaterializedSource.class, ThroughMaterializedActor.class, pushBasedSink, source, Stream.DEFAULT_POLL_INTERVAL);
        return stage.actorFor(Materialized.class, ComposedMaterialized.class, Arrays.asList(selfSource, pushBasedSink, sink));
    }

    public interface PushBasedSink<T> {
        void enqueue(Record<T> record);
        Completes<MaterializedSource> asSource();
    }

    public static class PushBasedTemporalMaterializedSink<T> extends Actor implements MaterializedSource, PushBasedSink<T> {
        private final Queue<Record<T>> queue;
        private final MaterializedSource selfAsMaterializedSource;

        public PushBasedTemporalMaterializedSink() {
            this.queue = new ArrayDeque<>(32);
            this.selfAsMaterializedSource = selfAs(MaterializedSource.class);
        }

        @Override
        public Completes<Optional<Record[]>> nextIfAny() {
            if (queue.isEmpty()) {
                completesEventually().with(Optional.empty());
            } else {
                completesEventually().with(Optional.of(queue.toArray(new Record[0])));
            }

            return completes();
        }

        @Override
        public Completes<MaterializedSource> asSource() {
            return completes().with(selfAsMaterializedSource);
        }

        @Override
        public void enqueue(Record<T> record) {
            queue.add(record);
        }
    }

    public static class ThroughMaterializedActor extends Actor implements MaterializedSource, Scheduled<Void> {
        private final int MAX_BUFFER = 32;

        private final PushBasedSink<Record> sink;
        private final MaterializedSource source;
        private final Queue<Record> queue;
        private final int pollInterval;
        private final Cancellable cancellable;
        private final MaterializedSource selfAsMaterializedSource;

        public ThroughMaterializedActor(PushBasedSink sink, MaterializedSource source, int pollInterval) {
            this.sink = sink;
            this.source = source;
            this.pollInterval = pollInterval;
            this.queue = new ArrayDeque<>(32);
            this.selfAsMaterializedSource = selfAs(MaterializedSource.class);
            this.cancellable = scheduler().schedule(selfAs(Scheduled.class), null, 0, pollInterval);
        }

        @Override
        public Completes<Optional<Record[]>> nextIfAny() {
            if (queue.isEmpty()) {
                completesEventually().with(Optional.empty());
            } else {
                completesEventually().with(Optional.of(queue.toArray(new Record[0])));
            }

            return completes();
        }

        @Override
        public Completes<MaterializedSource> asSource() {
            return completes().with(selfAsMaterializedSource);
        }

        @Override
        public void intervalSignal(Scheduled<Void> scheduled, Void data) {
            if (queue.size() >= MAX_BUFFER) {
                return;
            }

            source.nextIfAny()
                    .andThenConsume(maybeRec -> maybeRec.ifPresent(recs -> queue.addAll(Arrays.asList(recs))))
                    .andFinallyConsume(maybeRec -> maybeRec.ifPresent(this::propagate));
        }

        @Override
        public void stop() {
            this.cancellable.cancel();
            super.stop();
        }

        private void propagate(Record[] records) {
            for (Record record : records) {
                sink.enqueue(record);
            }
        }
    }
}
