// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams;

import io.vlingo.common.Completes;

/**
 * Transform a value of type {@code T} to a value of type {@code R}.
 *
 * @param <T> the type of the original value
 * @param <R> the type of the resulting value
 *
 * @see StreamTransformer
 */
@FunctionalInterface
public interface Transformer<T,R> {
  /**
   * Answer the {@code Completes<R>} value after transforming it from the {@code value} of type {@code T}.
   * @param value the T typed original value
   * @return {@code Completes<R>}
   */
  Completes<R> transform(final T value);
}
