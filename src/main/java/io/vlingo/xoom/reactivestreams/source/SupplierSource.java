// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.reactivestreams.source;

import java.util.function.Supplier;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.reactivestreams.Elements;
import io.vlingo.xoom.reactivestreams.Source;

/**
 * A Source that uses a {@code Supplier<T>} to provide next {@code Element<T>} instances.
 *
 * @param <T> the T type of Element being supplied
 */
public class SupplierSource<T> implements Source<T> {
  private final boolean slowSupplier;
  private final Supplier<T> supplier;

  public SupplierSource(final Supplier<T> supplier, final boolean slowSupplier) {
    this.supplier = supplier;
    this.slowSupplier = slowSupplier;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Completes<Elements<T>> next() {
    final T any = supplier.get();

    if (any != null) {
      final T[] elements = (T[]) new Object[1];
      elements[0] = any;
      return Completes.withSuccess(new Elements<>(elements, false));
    }

    return Completes.withSuccess(new Elements<>((T[]) new Object[0], true));
  }

  @Override
  public Completes<Elements<T>> next(final int maximumElements) {
    return next();
  }

  @Override
  public Completes<Elements<T>> next(final long index) {
    return next();
  }

  @Override
  public Completes<Elements<T>> next(final long index, final int maximumElements) {
    return next();
  }

  @Override
  public Completes<Boolean> isSlow() {
    return Completes.withSuccess(slowSupplier);
  }
}
