import io.vlingo.pipes.Streams;
import io.vlingo.pipes.sinks.PrintSink;
import io.vlingo.pipes.sources.CollectionSource;

public class MyApp {
    public static void main(String[] args) throws InterruptedException {
        var streams = Streams.app("my-app");

        streams.from(CollectionSource.fromArray(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
                .map(e -> e * 5)
                .filter(e -> (e % 2) == 0)
                .to(PrintSink.stdout("ps1> "));

        Thread.sleep(1000);
        streams.close();
    }
}
