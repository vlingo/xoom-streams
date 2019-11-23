package io.vlingo.pipes;

public interface Sink<T> extends Materializable {
    void whenValue(Record<T> value);
}
