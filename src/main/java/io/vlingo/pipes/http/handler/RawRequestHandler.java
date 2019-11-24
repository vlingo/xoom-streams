package io.vlingo.pipes.http.handler;

import io.vlingo.actors.Logger;
import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.http.Method;
import io.vlingo.http.Request;
import io.vlingo.http.Response;
import io.vlingo.http.resource.Action;
import io.vlingo.http.resource.RequestHandler;
import io.vlingo.pipes.Record;
import io.vlingo.pipes.Source;
import io.vlingo.pipes.Stream;
import io.vlingo.pipes.actor.Materialized;
import io.vlingo.pipes.actor.MaterializedSource;
import io.vlingo.pipes.actor.MaterializedSourceActor;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;

public class RawRequestHandler extends RequestHandler implements Source<Request> {
    private final Stage stage;
    private final Queue<Record<Request>> input;

    public RawRequestHandler(Method method, String path, Stage stage) {
        super(method, path, Collections.emptyList());
        this.stage = stage;
        this.input = new ArrayDeque<>(32);
    }

    @Override
    protected Completes<Response> execute(Request var1, Action.MappedParameters var2, Logger var3) {
        Completes<Response> completes = Completes.using(stage.scheduler());
        Record<Request> request = Record.of(var1).withMetadata("completes", completes);

        input.add(request);
        return completes;
    }

    @Override
    public CompletableFuture<Record<Request>[]> poll() {
        CompletableFuture<Record<Request>[]> result = CompletableFuture.completedFuture(input.toArray(new Record[0]));
        input.clear();

        return result;
    }

    @Override
    public Materialized materialize(Stage stage, MaterializedSource source) {
        return stage.actorFor(MaterializedSource.class, MaterializedSourceActor.class, this, Stream.DEFAULT_POLL_INTERVAL);
    }
}
