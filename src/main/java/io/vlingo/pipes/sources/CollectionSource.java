package io.vlingo.pipes.sources;

import io.vlingo.actors.Stage;
import io.vlingo.pipes.Record;
import io.vlingo.pipes.Source;
import io.vlingo.pipes.Stream;
import io.vlingo.pipes.actor.Materialized;
import io.vlingo.pipes.actor.MaterializedSource;
import io.vlingo.pipes.actor.MaterializedSourceActor;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class CollectionSource<T> implements Source<T> {
    private final static Object[] EMPTY = new Object[0];

    private final Collection<T> elements;
    private boolean consumed;

    public CollectionSource(Collection<T> elements) {
        this.elements = elements;
        consumed = false;
    }

    public static <T> CollectionSource<T> fromArray(T... t) {
        return new CollectionSource<>(Arrays.asList(t));
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<Record<T>[]> poll() {
        if (consumed) {
            return CompletableFuture.completedFuture((Record<T>[]) EMPTY);
        }

        consumed = true;
        Record<T>[] result = elements.stream().map(Record::of).toArray(Record[]::new);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public Materialized materialize(Stage stage, MaterializedSource source) {
        return stage.actorFor(MaterializedSource.class, MaterializedSourceActor.class, this, Stream.DEFAULT_POLL_INTERVAL);
    }
}
