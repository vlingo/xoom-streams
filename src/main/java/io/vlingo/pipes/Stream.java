package io.vlingo.pipes;

import io.vlingo.actors.Stage;
import io.vlingo.actors.Stoppable;
import io.vlingo.pipes.actor.Materialized;
import io.vlingo.pipes.operator.Filter;
import io.vlingo.pipes.operator.Map;

import java.io.Closeable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Stream<B, E> implements Closeable {
    public static final int DEFAULT_POLL_INTERVAL = 1;

    private Stage stage;
    private Source<B> source;
    private List<Operator<?, ?>> operators;
    private Sink<E> sink;
    private List<Stoppable> stoppables;

    private Stream(Stage stage, Source<B> source) {
        this.stage = stage;
        this.operators = new ArrayList<>(8);
        this.source = source;
        this.stoppables = new LinkedList<>();
    }

    public static <X> Stream<X, X> within(Stage stage, Source<X> source) {
        return new Stream<>(stage, source);
    }

    @SuppressWarnings("unchecked")
    public <X> Stream<B, X> map(Function<E, X> mapper) {
        this.operators.add(new Map<>(new ArrayDeque<>(32), mapper));
        return (Stream<B, X>) this;
    }

    @SuppressWarnings("unchecked")
    public Stream<B, E> filter(Predicate<E> filter) {
        this.operators.add(new Filter<>(new ArrayDeque<>(32), filter));
        return this;
    }

    public Closeable to(Sink<E> sink) {
        this.sink = sink;
        return this;
    }

    public void materialize() {
        Materialized operatorSource = source.materialize(stage, null);
        stoppables.add(operatorSource);
        for (Operator<?, ?> op : operators) {
            operatorSource = op.materialize(stage, operatorSource.asSource().await());
            stoppables.add(operatorSource);
        }

        Stoppable stoppable = sink.materialize(stage, operatorSource.asSource().await());
        stoppables.add(stoppable);
    }

    @Override
    public void close() {
        this.stoppables.forEach(Stoppable::stop);
    }
}
