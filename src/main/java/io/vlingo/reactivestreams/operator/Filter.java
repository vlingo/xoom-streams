// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams.operator;

import java.util.function.Consumer;
import java.util.function.Predicate;

import io.vlingo.reactivestreams.Operator;
import io.vlingo.reactivestreams.Streams;

/**
 * Filters {@code Sink<T>} values and potentially produces {@code Source<T>} values.
 * @param <T> the input and output type
 */
public class Filter<T> implements Operator<T,T> {
  private final Predicate<T> predicate;

  public Filter(final Predicate<T> predicate) {
    this.predicate = predicate;
  }

  @Override
  public void performInto(final T value, final Consumer<T> consumer) {
    try {
      if (predicate.test(value)) {
        consumer.accept(value);
      }
    } catch (Exception e) {
      Streams.logger().error("Filter failed because: " + e.getMessage(), e);
    }
  }
}
