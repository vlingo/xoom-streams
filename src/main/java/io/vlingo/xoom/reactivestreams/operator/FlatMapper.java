// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.reactivestreams.operator;

import java.util.function.Consumer;
import java.util.function.Function;

import io.vlingo.xoom.reactivestreams.Operator;
import io.vlingo.xoom.reactivestreams.Source;
import io.vlingo.xoom.reactivestreams.Streams;

public class FlatMapper<T, R> implements Operator<T,R> {
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
