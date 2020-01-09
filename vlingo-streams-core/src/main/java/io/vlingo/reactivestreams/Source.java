// Copyright © 2012-2019 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;

import io.vlingo.common.Completes;
import io.vlingo.reactivestreams.source.IterableSource;
import io.vlingo.reactivestreams.source.LongRangeSource;
import io.vlingo.reactivestreams.source.SupplierSource;

/**
 * The upstream source of a {@code Stream} that provides {@code Element<T>} of
 * next available value(s), or indicates termination of such values.
 * <p>
 * WARNING:
 * <p>
 * A {@code Source} is polled asynchronously by a {@code StreamPublisher}.
 * The {@code StreamPublisher} uses a {@link io.vlingo.common.Scheduler}
 * to determine the time interval between polling, and could assume the
 * rapid answer of the next element(s) or empty. Consider possible flaws
 * in such a design:
 * <p>
 * (1) Latency in {@code next()} and {@code next(int index)} may cause
 * publish conflicts given that the subsequent {@code intervalSignal()} is
 * delivered and its {@code next()} or {@code next(int index)} completes
 * before the previous publish completes. This would no doubt cause races
 * in any managed subscription instances.
 * <p>
 * (2) Latency in {@code next()} and {@code next(int index)} may also cause
 * eventual {@code OutOfMemoryException} due to {@code intervalSignal()}
 * messages growing the {@code StreamPublisher}'s mailbox faster than
 * it can process them.
 * <p>
 * In order to avoid this situation {@code isSlow()} is provided. If
 * {@code isSlow()} answers {@code true} the {@code StreamPublisher}
 * will reschedule its given time interval following the completion
 * of each {@code next()}/{@code next(int index)} and publish pair.
 * Although doubtful, this could tax the scheduler while preventing
 * the above two potential flaws.
 * <p>
 * If {@code isSlow()} answers {@code false} the {@code StreamPublisher}
 * will schedule a recurring time interval, which is done only once.
 *
 * @param <T> the type produced by the {@code Source}
 */
public interface Source<T> {
  /**
   * Answer a new empty {@code Source<T>}.
   * @param <T> the type of Source elements
   * @return {@code Source<T>}
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  static <T> Source<T> empty() {
    return new IterableSource(new ArrayList<>(0), false);
  }

  /**
   * Answer a new {@code Source<T>} with the static
   * number of {@code elements}.
   * @param elements the T typed element(s) provided by the new {@code Source<T>}
   * @param <T> the type of Source elements
   * @return {@code Source<T>}
   */
  @SafeVarargs
  @SuppressWarnings({ "unchecked", "rawtypes" })
  static <T> Source<T> only(final T... elements) {
    return new IterableSource(Arrays.asList(elements), false);
  }

  /**
   * Answer a new {@code Source<Long>} with element(s) to be provided between
   * {@code startInclusive} and {@code endExclusive}. The {@code Source<Long>} is
   * non-slow.
   * @param startInclusive the long start of the range, inclusive
   * @param endExclusive the long end of the range, exclusive
   * @return {@code Source<Long>}
   */
  static Source<Long> rangeOf(final long startInclusive, final long endExclusive) {
    return new LongRangeSource(startInclusive, endExclusive);
  }

  /**
   * Answer the number of {@code elements}, or if {@code elements} is out of bounds,
   * answer {@code Long.MAX_VALUE}.
   * @param elements the long number of elements, which may be in bounds or out of bounds
   * @return long
   */
  static long orElseMaximum(final long elements) {
    if (elements < 0) {
      return Long.MAX_VALUE;
    }
    return elements;
  }

  /**
   * Answer the number of {@code elements}, or if {@code elements} is out of bounds,
   * answer {@code 0}.
   * @param elements the long number of elements, which may be in bounds or out of bounds
   * @return long
   */
  static long orElseMinimum(final long elements) {
    if (elements < 0) {
      return 0;
    }
    return elements;
  }

  /**
   * Answer a new {@code Source<T>} with element(s) to be provided
   * by the {@code Iterable<T>}. The {@code Source<T>} is created
   * as non-slow.
   * @param iterable the {@code Iterable<T>} providing element(s) for the new {@code Source<T>}
   * @param <T> the type of Source elements
   * @return {@code Source<T>}
   */
  static <T> Source<T> with(final Iterable<T> iterable) {
    return with(iterable, false);
  }

  /**
   * Answer a new {@code Source<T>} with element(s) to be provided by the
   * {@code Iterable<T>}, which may or may not be a {@code slowIterable}
   * @param iterable the {@code Iterable<T>} providing element(s) for the new {@code Source<T>}
   * @param slowIterable the boolean indicating whether or not the iterable is slow
   * @param <T> the type of Source elements
   * @return {@code Source<T>}
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  static <T> Source<T> with(final Iterable<T> iterable, final boolean slowIterable) {
    return new IterableSource(iterable, slowIterable);
  }

  /**
   * Answer a new {@code Source<T>} with elements to be provided by the {@code Supplier<T>}.
   * The {@code Source<T>} is created created as non-slow.
   * @param supplier the {@code Supplier<T>} providing element(s) for the new {@code Source<T>}
   * @param <T> the type of Source elements
   * @return {@code Source<T>}
   */
  static <T> Source<T> with(final Supplier<T> supplier) {
    return with(supplier, false);
  }

  /**
   * Answer a new {@code Source<T>} with elements to be provided by the {@code Supplier<T>},
   * which may or may not be a {@code slowSupplier}.
   * @param supplier the {@code Supplier<T>} providing element(s) for the new {@code Source<T>}
   * @param slowSupplier the boolean indicating whether or not the supplier is slow
   * @param <T> the type of Source elements
   * @return {@code Source<T>}
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  static <T> Source<T> with(final Supplier<T> supplier, final boolean slowSupplier) {
    return new SupplierSource(supplier, slowSupplier);
  }

  /**
   * Answers the next element(s) as a {@code Completes<Elements<T>>}, which has a
   * zero length {@code values} when the next element is not <strong>immediately</strong> available.
   * Answering the zero length {@code Completes<Elements<T>>.value} is to prevent blocking.
   * @return {@code Completes<Elements<T>>}
   */
  Completes<Elements<T>> next();

  /**
   * Answers the next element(s) starting at {@code index} as a {@code Completes<Elements<T>>},
   * which has a zero length {@code values} when at least the indexed element is not
   * <strong>immediately</strong> available. Answering the zero length {@code Completes<Elements<T>>.value}
   * is to prevent blocking.
   * <p>
   * It is recommended to use this method only when the elements are actually
   * identified by indexes, such as with an ordered collection or log.
   *
   * @param index the int index of the element at which to start
   * @return {@code Completes<Elements<T>>}
   */
  Completes<Elements<T>> next(final long index);

  /**
   * Answers whether or not the concrete {@code Source} is subject to
   * constantly latency or intermittent latency. See the warning in the
   * above interface documentation.
   * @return {@code Completes<Boolean>}
   */
  Completes<Boolean> isSlow();
}
