package io.vlingo.pipes.http;

import io.vlingo.actors.Stage;
import io.vlingo.http.Method;
import io.vlingo.http.Request;
import io.vlingo.http.Response;
import io.vlingo.http.resource.*;
import io.vlingo.pipes.Sink;
import io.vlingo.pipes.Source;
import io.vlingo.pipes.http.handler.RawRequestHandler;
import io.vlingo.pipes.http.handler.ResponseSender;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import static io.vlingo.http.resource.ResourceBuilder.resource;

public class HttpStreamServer implements Closeable {
    private final Stage stage;
    private final List<RequestHandler> handlers;
    private Server server;

    public HttpStreamServer(Stage stage) {
        this.stage = stage;
        this.handlers = new ArrayList<>();
        this.server = null;
    }

    public Source<Request> requestSource(Method method, String path) {
        RawRequestHandler handler = new RawRequestHandler(method, path, stage);
        this.handlers.add(handler);
        return handler;
    }

    public Sink<Response> responseSink() {
        return new ResponseSender();
    }

    public void start(Stage stage, int port) {
        if (!handlers.isEmpty()) {
            Resource resource = resource("http-stream", 10, handlers.toArray(new RequestHandler[0]));
            this.server = Server.startWith(stage, Resources.are(resource), port, Configuration.Sizing.define(), Configuration.Timing.define());
        }
    }

    @Override
    public void close() {
        if (server != null) {
            server.stop();
        }
    }
}
