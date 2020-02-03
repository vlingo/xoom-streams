// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import io.vlingo.reactivestreams.operator.Filter;
import io.vlingo.reactivestreams.operator.Mapper;

/**
 * An operator that takes {@code T} input and produces {@code R} output
 * into a given {@code Consumer<R>}.
 * @param <T> the input parameter type
 * @param <R> the result type
 */
public interface Operator<T,R> {
  /**
   * Answer a new {@code Operator<T,T>} that filters using {@code filter}.
   * @param filter the {@code Predicate<T>} that filters
   * @param <T> the type of values to filter
   * @return {@code Operator<T,T>}
   */
  static <T> Operator<T,T> filterWith(final Predicate<T> filter) {
    return new Filter<>(filter);
  }

  /**
   * Answer a new {@code Operator<T,R>} that maps from values of {@code T}
   * to values of {@code R} by means of the {@code mapper}.
   * @param mapper the {@code Function<T,R>} that maps from values of T to values of R
   * @param <T> the type of values to mapped from
   * @param <R> the type of values mapped to
   * @return {@code Operator<T,R>}
   */
  static <T,R> Operator<T,R> mapWith(final Function<T,R> mapper) {
    return new Mapper<>(mapper);
  }

  /**
   * Accept the {@code T} value and potentially give an
   * {@code R} outcome to the {@code consumer}.
   * @param value the T value to accept
   * @param consumer the {@code Consumer<R>} that may receive the outcome.
   */
  void performInto(final T value, final Consumer<R> consumer);
}
