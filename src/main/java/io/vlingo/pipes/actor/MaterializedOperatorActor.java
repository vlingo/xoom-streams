// Copyright © 2012-2019 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.pipes.actor;

import io.vlingo.actors.Actor;
import io.vlingo.common.Cancellable;
import io.vlingo.common.Completes;
import io.vlingo.common.Scheduled;
import io.vlingo.pipes.Operator;
import io.vlingo.pipes.Record;

import java.util.*;
import java.util.stream.Stream;

public class MaterializedOperatorActor extends Actor implements MaterializedSource, Scheduled<Void> {
    private final int MAX_BUFFER = 32;

    private final MaterializedSource previousSource;
    private final Operator<Object, Object> operator;
    private final int pollingInterval;
    private final Cancellable cancellable;
    private final Queue<Record> queue;
    private MaterializedSource selfAsMaterializedSource;

    public MaterializedOperatorActor(MaterializedSource previousSource, Operator<Object, Object> operator, int pollingInterval) {
        this.selfAsMaterializedSource = selfAs(MaterializedSource.class);
        this.previousSource = previousSource;
        this.operator = operator;
        this.pollingInterval = pollingInterval;
        this.queue = new ArrayDeque<>(MAX_BUFFER);
        this.cancellable = scheduler().schedule(selfAs(Scheduled.class), null, 0, pollingInterval);
    }

    @Override
    public void intervalSignal(Scheduled<Void> scheduled, Void aVoid) {
        if (queue.size() >= MAX_BUFFER) {
            return;
        }

        previousSource.nextIfAny()
                .andThenConsume((e) -> operator.poll().thenAccept(record -> queue.addAll(Arrays.asList(record))))
                .andFinallyConsume(e -> e.ifPresent(this::whenValueForEach));
    }

    @Override
    public Completes<Optional<Record[]>> nextIfAny() {
        if (queue.isEmpty()) {
            return completes().with(Optional.empty());
        }

        int size = queue.size();
        Record[] res = new Record[size];
        for (int i = 0; i < size; i++) {
            res[i] = queue.poll();
        }

        return completes().with(Optional.ofNullable(res));
    }

    @Override
    public void stop() {
        this.cancellable.cancel();
        super.stop();
    }

    @Override
    public Completes<MaterializedSource> asSource() {
        return completes().with(selfAsMaterializedSource);
    }

    private void whenValueForEach(Record[] e) {
        Stream.of(e).forEach(operator::whenValue);
    }
}
