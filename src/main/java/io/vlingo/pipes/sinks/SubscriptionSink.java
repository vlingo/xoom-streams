package io.vlingo.pipes.sinks;

import io.vlingo.actors.Stage;
import io.vlingo.pipes.Sink;
import io.vlingo.pipes.Stream;
import io.vlingo.pipes.actor.Materialized;
import io.vlingo.pipes.actor.MaterializedSinkActor;
import io.vlingo.pipes.actor.MaterializedSource;

import java.util.function.Consumer;

public class SubscriptionSink<T> implements Sink<T> {
    private final Consumer<T> subscriber;

    private SubscriptionSink(Consumer<T> subscriber) {
        this.subscriber = subscriber;
    }

    public static <T> SubscriptionSink<T> subscribingWith(Consumer<T> subscriber) {
        return new SubscriptionSink<>(subscriber);
    }

    @Override
    public void whenValue(T value) {
        subscriber.accept(value);
    }

    @Override
    public Materialized materialize(Stage stage, MaterializedSource source) {
        return stage.actorFor(Materialized.class, MaterializedSinkActor.class, source, this, Stream.DEFAULT_POLL_INTERVAL);
    }
}
