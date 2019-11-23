package io.vlingo.pipes;

public interface Operator<A, B> extends Source<B>, Sink<A> {
}
