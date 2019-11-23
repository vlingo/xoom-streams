package io.vlingo.pipes.operator;

import io.vlingo.actors.Stage;
import io.vlingo.actors.Stoppable;
import io.vlingo.pipes.actor.MaterializedSource;

import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class Map<A, B> extends BasicOperator<A, B> {
    private final Queue<B> output;
    private final Function<A, B> mapper;

    public Map(Queue<B> output, Function<A, B> mapper) {
        this.output = output;
        this.mapper = mapper;
    }

    @Override
    public void whenValue(A value) {
        output.add(mapper.apply(value));
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<B[]> poll() {
        var result = CompletableFuture.completedFuture((B[]) output.toArray());
        output.clear();

        return result;
    }
}
