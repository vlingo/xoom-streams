package io.vlingo.pipes;

import io.vlingo.common.Tuple2;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Record<T> {
    private final UUID correlationId;
    private final Map<String, Object> metadata;
    private final T value;

    private Record(UUID correlationId, Map<String, Object> metadata, T value) {
        this.correlationId = correlationId;
        this.metadata = metadata;
        this.value = value;
    }

    public static <T> Record<T> of(T value) {
        return new Record<>(UUID.randomUUID(), new HashMap<>(0), value);
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

    public <K> Record<Tuple2<T, K>> mergeLeft(Record<K> k) {
        UUID correlationId = UUID.randomUUID();
        k.metadata.forEach((key, value1) -> metadata.merge(key, value1, (ek, ev) -> ev));
        k.metadata.put("leftCorrelationId", this.correlationId);
        k.metadata.put("rightCorrelationId", k.correlationId);

        return new Record<>(correlationId, metadata, Tuple2.from(value, k.value));
    }
    @Override
    public String toString() {
        return "Record{" +
                "correlationId=" + correlationId +
                ", metadata=" + metadata +
                ", value=" + value +
                '}';
    }
}
