package io.vlingo.pipes;

import io.vlingo.actors.Stage;
import io.vlingo.actors.Stoppable;
import io.vlingo.common.Tuple2;
import io.vlingo.pipes.actor.Materialized;
import io.vlingo.pipes.operator.Filter;
import io.vlingo.pipes.operator.Map;
import io.vlingo.pipes.operator.Through;
import io.vlingo.pipes.operator.ZipWithSource;
import io.vlingo.pipes.sources.CollectionSource;

import java.io.Closeable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Stream<B, E> implements Closeable {
    public static final int DEFAULT_POLL_INTERVAL = 5;

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

    @SuppressWarnings("unchecked")
    public <K> Stream<B, Tuple2<E, K>> zip(Source<K> source) {
        this.operators.add(new ZipWithSource<>(source, new ArrayDeque<>(32)));
        return (Stream<B, Tuple2<E, K>>) this;
    }

    public <K> Stream<B, Tuple2<E, K>> zip(Iterable<K> source) {
        this.operators.add(new ZipWithSource<>(CollectionSource.fromIterable(source), new ArrayDeque<>(32)));
        return (Stream<B, Tuple2<E, K>>) this;
    }

    public Stream<B, E> through(Sink<E> sink) {
        this.operators.add(new Through<>(sink, new ArrayDeque<>(32)));
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
