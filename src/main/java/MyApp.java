import io.vlingo.http.Method;
import io.vlingo.http.Response;
import io.vlingo.pipes.Streams;
import io.vlingo.pipes.sinks.SubscriptionSink;

public class MyApp {
    public static void main(String[] args) throws InterruptedException {
        var streams = Streams.app("my-app");

        streams.from(streams.http.requestSource(Method.POST, "/"))
                .map(e -> ">> " + e.body.content())
                .map(String::toUpperCase)
                .map(e -> Response.of(Response.Status.Ok, e))
                .to(streams.http.responseSink());

        streams.from(streams.http.requestSource(Method.GET, "/close"))
                .to(SubscriptionSink.subscribingWith(request -> streams.close()));

        streams.start();
    }
}
