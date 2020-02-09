// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams.operator;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import io.vlingo.reactivestreams.Operator;
import io.vlingo.reactivestreams.Streams;

/**
 * Flat maps from {@code T} to {@code R}. Flat map is a combination of
 * {@code map()} and {@code flatten()}, which maps {@code T} to {@code R}
 * and then flattens the inner type.
 * @param <T> the input parameter type
 * @param <R> the result type
 */
public class FlatMapper<T,R> implements Operator<T,R> {
  private final Function<T,R> mapper;

  public FlatMapper(final Function<T,R> mapper) {
    this.mapper = mapper;
  }

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void performInto(final T value, final Consumer<R> consumer) {
    try {
      if (value instanceof Collection) {
        ((Collection) value).stream().flatMap(mapper).forEach(consumer);
      } else if (value instanceof Stream) {
        ((Stream) value).flatMap(mapper).forEach(consumer);
      } else if (value instanceof Optional) {
        ((Optional) value).flatMap(mapper).ifPresent(consumer);
      } else if (value instanceof String) {
        ((String) value).chars().forEach((r) -> consumer.accept(toOther(r)));
      } else {
        consumer.accept(mapper.apply(value));
      }
    } catch (Exception e) {
      System.out.println("Flat mapper failed because: " + e.getMessage());
      e.printStackTrace();
      Streams.logger().error("Flat mapper failed because: " + e.getMessage(), e);
    }
  }

  @SuppressWarnings("unchecked")
  private <S,O> O toOther(final S r) {
    return (O) r;
  }
}
