// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams.operator;

import java.util.function.Consumer;
import java.util.function.Function;

import io.vlingo.reactivestreams.Operator;
import io.vlingo.reactivestreams.Streams;

/**
 * Maps from {@code Sink<T>} to {@code Source<R>}.
 * @param <T> the input parameter type
 * @param <R> the result type
 */
public class Mapper<T,R> implements Operator<T,R> {
  private final Function<T,R> mapper;

  public Mapper(final Function<T,R> mapper) {
    this.mapper = mapper;
  }

  @Override
  public void performInto(final T value, Consumer<R> consumer) {
    try {
      consumer.accept(mapper.apply(value));
    } catch (Exception e) {
      Streams.logger().error("Mapper failed because: " + e.getMessage(), e);
    }
  }
}
