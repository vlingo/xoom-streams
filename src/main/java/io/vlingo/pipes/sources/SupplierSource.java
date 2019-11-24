package io.vlingo.pipes.sources;

import io.vlingo.actors.Stage;
import io.vlingo.pipes.Record;
import io.vlingo.pipes.Source;
import io.vlingo.pipes.Stream;
import io.vlingo.pipes.actor.Materialized;
import io.vlingo.pipes.actor.MaterializedSource;
import io.vlingo.pipes.actor.MaterializedSourceActor;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class SupplierSource<T> implements Source<T> {
    private final Supplier<T> supplier;

    public SupplierSource(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> SupplierSource<T> fromSupplier(Supplier<T> t) {
        return new SupplierSource<>(t);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<Record<T>[]> poll() {
        Record<T>[] result = new Record[] { Record.of(supplier.get()) };
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public Materialized materialize(Stage stage, MaterializedSource source) {
        return stage.actorFor(MaterializedSource.class, MaterializedSourceActor.class, this, Stream.DEFAULT_POLL_INTERVAL);
    }
}
