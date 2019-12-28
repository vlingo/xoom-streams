package io.vlingo.pipes.operator;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

// Copyright © 2012-2019 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

import io.vlingo.pipes.Record;

public class Delay<A> extends BasicOperator<A, A> {
    private final Queue<Record<A>> output;
    private final int time;
    private final TimeUnit unit;
    private final Executor executor;

    public Delay(Queue<Record<A>> output, int time, TimeUnit unit) {
        this.output = output;
        this.time = time;
        this.unit = unit;
        this.executor = null; // CompletableFuture.delayedExecutor(time, unit);
    }

    @Override
    public void whenValue(Record<A> value) {
        CompletableFuture.supplyAsync(() -> value, executor)
                .thenAccept(output::add);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<Record<A>[]> poll() {
        CompletableFuture<Record<A>[]> result = CompletableFuture.completedFuture(output.toArray(new Record[0]));
        output.clear();

        return result;
    }
}
