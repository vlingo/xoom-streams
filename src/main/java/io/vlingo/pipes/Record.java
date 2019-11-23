package io.vlingo.pipes;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public final class Record<T> {
    private final UUID correlationId;
    private final Map<String, Object> metadata;
    private final T value;

    public Record(UUID correlationId, Map<String, Object> metadata, T value) {
        this.correlationId = correlationId;
        this.metadata = metadata;
        this.value = value;
    }

    public <N> Record<N> withValue(N newValue) {
        return new Record<>(correlationId, metadata, newValue);
    }

    public Record<T> withMetadata(String key, Object value) {
        metadata.put(key, value);
        return this;
    }

    public UUID correlationId() {
        return correlationId;
    }

    public Map<String, Object> metadata() {
        return Collections.unmodifiableMap(metadata);
    }

    public T value() {
        return value;
    }
}
