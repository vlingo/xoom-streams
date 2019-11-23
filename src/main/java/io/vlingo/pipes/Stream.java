package io.vlingo.pipes;

import io.vlingo.actors.Stage;
import io.vlingo.actors.Stoppable;
import io.vlingo.pipes.operator.Filter;
import io.vlingo.pipes.operator.Map;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
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
        this.operators.add(new Map<>(new PriorityQueue<>(32), mapper));
        return (Stream<B, X>) this;
    }

    @SuppressWarnings("unchecked")
    public Stream<B, E> filter(Predicate<E> filter) {
        this.operators.add(new Filter<>(new PriorityQueue<>(32), filter));
        return this;
    }

    public Closeable to(Sink<E> sink) {
        this.sink = sink;

        var operatorSource = source.materialize(stage, null);
        this.stoppables.add(operatorSource);
        for (var op : operators) {
            operatorSource = op.materialize(stage, operatorSource.asSource().await());
            this.stoppables.add(operatorSource);
        }

        var stoppable = sink.materialize(stage, operatorSource.asSource().await());
        this.stoppables.add(stoppable);

        return this;
    }

    @Override
    public void close() {
        this.stoppables.forEach(Stoppable::stop);
    }
}
