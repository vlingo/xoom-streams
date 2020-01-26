// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A container for the element(s) that may be available on each
 * {@code Source<T>#next()} or {@code Source<T>#next(int index)}.
 *
 * @param <T> the type of element value
 */
public class Elements<T> {
  private static final Object[] Empty = new Object[0];

  /**
   * Zero or more element values.
   */
  public final T[] values;

  /**
   * Boolean {@code true} when the {@code Source<T>} has no more elements;
   * otherwise {@code false}.
   */
  public final boolean terminated;

  /**
   * Answer a new {@code Elements<T>} with no {@code values} but that is not {@code terminated}.
   * @param <T> type type of the {@code Elements<T>}
   * @return {@code Elements<T>}
   */
  @SuppressWarnings("unchecked")
  public static final <T> Elements<T> empty() {
    return new Elements<>((T[]) Empty, false);
  }

  /**
   * Answer a new {@code Elements<T>} with the single {@code value} of {@code elements}.
   * @param value the T typed value instance of the new {@code Elements<T>}
   * @param <T> type type of the {@code Elements<T>}
   * @return {@code Elements<T>}
   */
  @SuppressWarnings("unchecked")
  public static final <T> Elements<T> of(final T value) {
    return new Elements<>((T[]) new Object[] { value }, false);
  }

  /**
   * Answer a new {@code Elements<T>} with {@code values} of {@code elements}.
   * @param values the T typed value instances of the new {@code Elements<T>}
   * @param <T> type type of the {@code Elements<T>}
   * @return {@code Elements<T>}
   */
  @SuppressWarnings("unchecked")
  public static final <T> Elements<T> of(final T... values) {
    return new Elements<>(values, false);
  }

  /**
   * Answer a new {@code Elements<T>} with no {@code values} and that is {@code terminated}.
   * @param <T> type type of the {@code Elements<T>}
   * @return {@code Elements<T>}
   */
  @SuppressWarnings("unchecked")
  public static final <T> Elements<T> terminated() {
    return new Elements<>((T[]) Empty, true);
  }

  /**
   * Constructs my state.
   * @param values the T[] of zero or more element values
   * @param terminated the boolean indicating whether or not the Source is terminated
   */
  public Elements(final T[] values, final boolean terminated) {
    this.values = values;
    this.terminated = terminated;
  }

  /**
   * Answer the {@code T} value at the {@code index}.
   * @param index the int index of the value to answer
   * @return T
   */
  public T elementAt(final int index) {
    if (values.length == 0) {
      return null;
    }
    return values[index];
  }

  /**
   * Answer whether or not there is an element value at {@code index}.
   * @param index the int index of the value to check for existence
   * @return boolean
   */
  public boolean hasElementAt(final int index) {
    if (values.length == 0) {
      return false;
    }
    return index < values.length;
  }

  /**
   * Answer the String representation of my element values.
   * @return String
   */
  public String elementsAsString() {
    return Arrays.asList(values).toString();
  }

  /**
   * Answer my {@code values} as a {@code List<T>}.
   * @return {@code List<T>}
   */
  public List<T> asList() {
    return Collections.unmodifiableList(Arrays.asList(values));
  }

  /**
   * Answer the number of element values.
   * @return int
   */
  public int size() {
    return values.length;
  }

  @Override
  public String toString() {
    return "Elements[" +
            "values=" + elementsAsString() +
            " terminated=" + terminated +
           "]";
  }
}
