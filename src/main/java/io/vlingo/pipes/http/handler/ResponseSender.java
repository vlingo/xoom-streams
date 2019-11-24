package io.vlingo.pipes.http.handler;

import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.http.Response;
import io.vlingo.pipes.Record;
import io.vlingo.pipes.Sink;
import io.vlingo.pipes.Stream;
import io.vlingo.pipes.actor.Materialized;
import io.vlingo.pipes.actor.MaterializedSinkActor;
import io.vlingo.pipes.actor.MaterializedSource;

public class ResponseSender implements Sink<Response> {
    @Override
    public void whenValue(Record<Response> value) {
        Completes<Response> completes = (Completes<Response>) value.metadata().get("completes");
        completes.with(value.value());
    }

    @Override
    public Materialized materialize(Stage stage, MaterializedSource source) {
        return stage.actorFor(Materialized.class, MaterializedSinkActor.class, source, this, Stream.DEFAULT_POLL_INTERVAL);
    }
}
