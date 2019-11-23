package io.vlingo.pipes;

import io.vlingo.actors.Stage;
import io.vlingo.actors.World;

import java.io.Closeable;
import java.util.LinkedList;
import java.util.List;

public class Streams implements Closeable {
    private final World world;
    private final List<Stream<?, ?>> streams;

    private Streams(World world) {
        this.world = world;
        this.streams = new LinkedList<>();
    }

    public static Streams app(final String name) {
        return new Streams(World.startWithDefaults(name));
    }

    public static Streams app(final Stage stage) {
        return new Streams(stage.world());
    }

    public <T> Stream<T, T> from(Source<T> source) {
        return Stream.within(world.stage(), source);
    }

    @Override
    public void close() {
        streams.forEach(Stream::close);
        world.terminate();
    }
}
