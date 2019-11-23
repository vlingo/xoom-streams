package io.vlingo.pipes.actor;

import io.vlingo.actors.Actor;
import io.vlingo.common.Cancellable;
import io.vlingo.common.Completes;
import io.vlingo.common.Scheduled;
import io.vlingo.pipes.Sink;

import java.util.stream.Stream;

public class MaterializedSinkActor extends Actor implements Materialized, Scheduled<Void> {
    private final MaterializedSource source;
    private final Sink<Object> sink;
    private final int pollingInterval;
    private final Cancellable cancellable;

    public MaterializedSinkActor(MaterializedSource source, Sink<Object> sink, int pollingInterval) {
        this.source = source;
        this.sink = sink;
        this.pollingInterval = pollingInterval;
        this.cancellable = scheduler().schedule(selfAs(Scheduled.class), null, 0, pollingInterval);
    }

    @Override
    public void intervalSignal(Scheduled<Void> scheduled, Void aVoid) {
        this.source.nextIfAny().andThenConsume(e -> e.ifPresent(this::whenValueForEach));
    }

    @Override
    public void stop() {
        this.cancellable.cancel();
        super.stop();
    }

    @Override
    public Completes<MaterializedSource> asSource() {
        return completes().with(null);
    }

    private void whenValueForEach(Object[] e) {
        Stream.of(e).forEach(sink::whenValue);
    }
}
