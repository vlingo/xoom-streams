import io.vlingo.http.Method;
import io.vlingo.http.Response;
import io.vlingo.pipes.Streams;
import io.vlingo.pipes.sinks.SubscriptionSink;
import io.vlingo.pipes.sources.SupplierSource;

import java.time.Instant;
import java.util.UUID;

import static io.vlingo.http.resource.serialization.JsonSerialization.serialized;

public class MyApp {
    public static void main(String[] args) throws InterruptedException {
        Streams streams = Streams.app("my-app");

        streams.from(streams.http.requestSource(Method.POST, "/"))
                .map(e -> e.body.content())
                .map(String::toUpperCase)
                .zip(SupplierSource.fromSupplier(UUID::randomUUID))
                .map(e -> Response.of(Response.Status.Ok, serialized(e)))
                .to(streams.http.responseSink());

        streams.from(streams.http.requestSource(Method.GET, "/close"))
                .map(e -> UUID.randomUUID().toString())
                .zip(SupplierSource.fromSupplier(Instant::now))
                .map(e -> Response.of(Response.Status.Ok, serialized(e)))
                .through(streams.http.responseSink())
                .to(SubscriptionSink.subscribingWith(request -> streams.close()));

        streams.start();
    }
}
