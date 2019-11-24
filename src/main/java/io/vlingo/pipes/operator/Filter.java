package io.vlingo.pipes.operator;

import io.vlingo.pipes.Record;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class Filter<A> extends BasicOperator<A, A> {
    private final Queue<Record<A>> output;
    private final Predicate<A> filter;

    public Filter(Queue<Record<A>> output, Predicate<A> filter) {
        this.output = output;
        this.filter = filter;
    }

    @Override
    public void whenValue(Record<A> value) {
        if (filter.test(value.value())) {
            output.add(value);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<Record<A>[]> poll() {
        CompletableFuture<Record<A>[]> result = CompletableFuture.completedFuture(output.toArray(new Record[0]));
        output.clear();

        return result;
    }
}
