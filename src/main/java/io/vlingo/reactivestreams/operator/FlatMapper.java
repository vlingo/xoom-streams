package io.vlingo.reactivestreams.operator;

import io.vlingo.common.Completes;
import io.vlingo.reactivestreams.Elements;
import io.vlingo.reactivestreams.Operator;
import io.vlingo.reactivestreams.Source;
import io.vlingo.reactivestreams.Streams;

import java.util.function.Consumer;
import java.util.function.Function;

public class FlatMapper<T,R> implements Operator<T,R> {
    private final Function<T, Source<R>> mapper;
    private final static int MAXIMUM_BUFFER = 32;

    public FlatMapper(Function<T, Source<R>> mapper) {
        this.mapper = mapper;
    }

    @Override
    public void performInto(T value, Consumer<R> consumer) {
        try {
            final Source<R> result = mapper.apply(value);
            propagateSource(result, consumer);
        } catch (Exception ex) {
            Streams.logger().error("FlatMapper failed because: " + ex.getMessage(), ex);
        }
    }

    private void propagateSource(Source<R> source, Consumer<R> consumer) {
        source.next(MAXIMUM_BUFFER).andFinallyConsume(elements -> {
            for (R element : elements.values) {
                consumer.accept(element);
            }

            if (!elements.terminated) {
                propagateSource(source, consumer);
            }
        });
    }
}
