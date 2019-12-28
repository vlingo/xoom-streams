// Copyright © 2012-2019 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams.source;

import java.util.Optional;
import java.util.function.Supplier;

import io.vlingo.common.Completes;
import io.vlingo.reactivestreams.Elements;
import io.vlingo.reactivestreams.Source;

public class SupplierSource<T> implements Source<T> {
  private Optional<T[]> lookAhead;
  private final boolean slowSupplier;
  private final Supplier<T> supplier;

  public SupplierSource(final Supplier<T> supplier, final boolean slowSupplier) {
    this.supplier = supplier;
    this.slowSupplier = slowSupplier;
    this.lookAhead = Optional.empty();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Completes<Elements<T>> next() {
    if (lookAhead.isPresent()) {
      final T[] next = lookAhead.get();
      lookAhead = Optional.empty();
      return Completes.withSuccess(new Elements<>(next, false));
    }

    final T any = supplier.get();

    if (any != null) {
      final T[] elements = (T[]) new Object[1];
      elements[0] = any;
      return Completes.withSuccess(new Elements<>(elements, false));
    }

    return Completes.withSuccess(new Elements<>((T[]) new Object[0], true));
  }

  @Override
  public Completes<Elements<T>> next(final long index) {
    return next();
  }

  @Override
  public Completes<Boolean> isSlow() {
    return Completes.withSuccess(slowSupplier);
  }
}
