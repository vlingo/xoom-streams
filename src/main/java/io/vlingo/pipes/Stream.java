package io.vlingo.pipes;

import io.vlingo.actors.Stage;
import io.vlingo.actors.Stoppable;
import io.vlingo.common.Completes;
import io.vlingo.common.Tuple2;
import io.vlingo.pipes.actor.Materialized;
import io.vlingo.pipes.operator.*;

import java.io.Closeable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
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

    public <X> Stream<B, X> lift(Operator<E, X> operator) {
        this.operators.add(operator);
        return (Stream<B, X>) this;
    }

    @SuppressWarnings("unchecked")
    public <X> Stream<B, X> map(Function<E, X> mapper) {
        return lift(new Map<>(new ArrayDeque<>(32), mapper));
    }

    @SuppressWarnings("unchecked")
    public <X> Stream<B, X> flatMapFuture(Function<E, CompletableFuture<X>> mapper) {
        return lift(new FlatMapCompletableFuture<>(new ArrayDeque<>(32), mapper));
    }

    @SuppressWarnings("unchecked")
    public <X> Stream<B, X> flatMapCompletes(Function<E, Completes<X>> mapper) {
        return lift(new FlatMapCompletes<>(new ArrayDeque<>(32), mapper));
    }

    @SuppressWarnings("unchecked")
    public Stream<B, E> filter(Predicate<E> filter) {
        return lift(new Filter<>(new ArrayDeque<>(32), filter));
    }

    @SuppressWarnings("unchecked")
    public <K> Stream<B, Tuple2<E, K>> zip(Source<K> source) {
        this.operators.add(new ZipWithSource<>(source, new ArrayDeque<>(32)));
        return (Stream<B, Tuple2<E, K>>) this;
    }

    public Stream<B, E> through(Sink<E> sink) {
        return lift(new Through<>(sink, new ArrayDeque<>(32)));
    }

    public Stream<B, E> delay(int time, TimeUnit unit) {
        return lift(new Delay<>(new ArrayDeque<>(32), time, unit));
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
