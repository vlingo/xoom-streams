package io.vlingo.pipes.operator;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class Filter<A> extends BasicOperator<A, A> {
    private final Queue<A> output;
    private final Predicate<A> filter;

    public Filter(Queue<A> output, Predicate<A> filter) {
        this.output = output;
        this.filter = filter;
    }

    @Override
    public void whenValue(A value) {
        if (filter.test(value)) {
            output.add(value);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<A[]> poll() {
        var result = CompletableFuture.completedFuture((A[]) output.toArray());
        output.clear();

        return result;
    }
}
