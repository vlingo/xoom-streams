package io.vlingo.pipes.sinks;

import io.vlingo.actors.Stage;
import io.vlingo.pipes.Record;
import io.vlingo.pipes.Sink;
import io.vlingo.pipes.Stream;
import io.vlingo.pipes.actor.Materialized;
import io.vlingo.pipes.actor.MaterializedSinkActor;
import io.vlingo.pipes.actor.MaterializedSource;

import java.io.PrintStream;

public class PrintSink<T> implements Sink<T> {
    private final PrintStream printStream;
    private final String prefix;

    public PrintSink(PrintStream printStream, String prefix) {
        this.printStream = printStream;
        this.prefix = prefix;
    }

    public static <T> PrintSink<T> stdout(String prefix) {
        return new PrintSink<>(System.out, prefix);
    }

    @Override
    public void whenValue(Record<T> value) {
        printStream.println(prefix + value.toString());
    }

    @Override
    public Materialized materialize(Stage stage, MaterializedSource source) {
        return stage.actorFor(Materialized.class, MaterializedSinkActor.class, source, this, Stream.DEFAULT_POLL_INTERVAL);
    }
}
