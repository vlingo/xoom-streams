package io.vlingo.pipes.operator;

import io.vlingo.pipes.Record;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class Map<A, B> extends BasicOperator<A, B> {
    private final Queue<Record<B>> output;
    private final Function<A, B> mapper;

    public Map(Queue<Record<B>> output, Function<A, B> mapper) {
        this.output = output;
        this.mapper = mapper;
    }

    @Override
    public void whenValue(Record<A> value) {
        B newValue = mapper.apply(value.value());
        output.add(value.withValue(newValue));
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<Record<B>[]> poll() {
        CompletableFuture<Record<B>[]> result = CompletableFuture.completedFuture(output.toArray(Record[]::new));
        output.clear();

        return result;
    }
}
