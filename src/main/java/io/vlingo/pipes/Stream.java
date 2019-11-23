package io.vlingo.pipes;

import io.vlingo.actors.Stage;
import io.vlingo.actors.Stoppable;
import io.vlingo.pipes.actor.MaterializedSource;
import io.vlingo.pipes.operator.Filter;
import io.vlingo.pipes.operator.Map;

import java.io.Closeable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class Stream<B, E> implements Closeable {
    public static final int DEFAULT_POLL_INTERVAL = 1;

    private Stage stage;
    private Source<B> source;
    private List<Operator<?, ?>> operators;
    private List<Sink<E>> sinks;
    private List<Stoppable> stoppables;

    private Stream(Stage stage, Source<B> source) {
        this.stage = stage;
        this.operators = new ArrayList<>(8);
        this.source = source;
        this.sinks = null;
        this.stoppables = new LinkedList<>();
    }

    public static <X> Stream<X, X> within(Stage stage, Source<X> source) {
        return new Stream<>(stage, source);
    }

    @SuppressWarnings("unchecked")
    public <X> Stream<B, X> map(Function<E, X> mapper) {
        this.operators.add(new Map<>(new PriorityQueue<>(32), mapper));
        return (Stream<B, X>) this;
    }

    @SuppressWarnings("unchecked")
    public Stream<B, E> filter(Predicate<E> filter) {
        this.operators.add(new Filter<>(new PriorityQueue<>(32), filter));
        return this;
    }

    public Closeable to(Sink<E> ...sinks) {
        return to(Arrays.asList(sinks));
    }

    public Closeable to(List<Sink<E>> sinks) {
        this.sinks = sinks;

        var operatorSource = source.materialize(stage, null);
        this.stoppables.add(operatorSource);
        for (var op : operators) {
            operatorSource = op.materialize(stage, operatorSource.asSource().await());
            this.stoppables.add(operatorSource);
        }

        MaterializedSource finalSource = operatorSource.asSource().await();
        for (var sink : this.sinks) {
            var stoppable = sink.materialize(stage, finalSource);
            this.stoppables.add(stoppable);
        }

        return this;
    }

    @Override
    public void close() {
        this.stoppables.forEach(Stoppable::stop);
    }
}
