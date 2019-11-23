package io.vlingo.pipes;

import java.util.Collections;
import java.util.Map;

public final class Message<T> {
    private final T value;
    private final Map<String, Object> metadata;

    public Message(T value, Map<String, Object> metadata) {
        this.value = value;
        this.metadata = metadata;
    }

    public final <N> Message<N> withValue(N newValue) {
        return new Message<>(newValue, metadata);
    }

    public final Message<T> withMetadata(String key, Object value) {
        metadata.put(key, value);
        return this;
    }

    public final T value() {
        return value;
    }

    public final Map<String, Object> metadata() {
        return Collections.unmodifiableMap(metadata);
    }
}
