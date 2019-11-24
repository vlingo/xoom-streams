package io.vlingo.pipes;

import io.vlingo.actors.Stage;
import io.vlingo.actors.World;
import io.vlingo.pipes.http.HttpStreamServer;

import java.io.Closeable;
import java.util.LinkedList;
import java.util.List;

public class Streams implements Closeable {
    private final World world;
    private final List<Stream<?, ?>> streams;
    public final HttpStreamServer http;

    private Streams(World world) {
        this.world = world;
        this.streams = new LinkedList<>();
        this.http = new HttpStreamServer(world.stage());
    }

    public static Streams app(final String name) {
        return new Streams(World.startWithDefaults(name));
    }

    public static Streams app(final Stage stage) {
        return new Streams(stage.world());
    }

    public <T> Stream<T, T> from(Source<T> source) {
        Stream<T, T> stream = Stream.within(world.stage(), source);
        streams.add(stream);

        return stream;
    }

    public void start() {
        streams.forEach(Stream::materialize);
        http.start(world.stage(), 8080);
    }

    @Override
    public void close() {
        streams.forEach(Stream::close);
        http.close();
        world.terminate();
    }
}
