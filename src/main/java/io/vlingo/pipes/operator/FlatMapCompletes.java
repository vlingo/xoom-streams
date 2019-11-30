package io.vlingo.pipes.operator;

import io.vlingo.common.Completes;
import io.vlingo.pipes.Record;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class FlatMapCompletes<A, B> extends BasicOperator<A, B> {
    private final Queue<Record<B>> output;
    private final Function<A, Completes<B>> mapper;

    public FlatMapCompletes(Queue<Record<B>> output, Function<A, Completes<B>> mapper) {
        this.output = output;
        this.mapper = mapper;
    }

    @Override
    public void whenValue(Record<A> value) {
        mapper.apply(value.value()).andFinallyConsume(v -> output.add(value.withValue(v)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<Record<B>[]> poll() {
        CompletableFuture<Record<B>[]> result = CompletableFuture.completedFuture(output.toArray(new Record[0]));
        output.clear();

        return result;
    }
}
