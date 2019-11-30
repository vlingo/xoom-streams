package io.vlingo.pipes.operator;

import io.vlingo.pipes.Record;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class Delay<A> extends BasicOperator<A, A> {
    private final Queue<Record<A>> output;
    private final int time;
    private final TimeUnit unit;
    private final Executor executor;

    public Delay(Queue<Record<A>> output, int time, TimeUnit unit) {
        this.output = output;
        this.time = time;
        this.unit = unit;
        this.executor = CompletableFuture.delayedExecutor(time, unit);
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
