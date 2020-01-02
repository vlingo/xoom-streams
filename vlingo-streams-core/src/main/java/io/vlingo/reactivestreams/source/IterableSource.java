// Copyright © 2012-2019 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams.source;

import java.util.Iterator;

import io.vlingo.common.Completes;
import io.vlingo.reactivestreams.Elements;
import io.vlingo.reactivestreams.Source;

public class IterableSource<T> implements Source<T> {
  private final Iterator<T> iterator;
  private final boolean slowIterable;

  public IterableSource(final Iterable<T> iterable, final boolean slowIterable) {
    this.iterator = iterable.iterator();
    this.slowIterable = slowIterable;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Completes<Elements<T>> next() {
    if (iterator.hasNext()) {
      final T[] element = (T[]) new Object[1];
      element[0] = iterator.next();
      return Completes.withSuccess(new Elements<>(element, false));
    }
    return Completes.withSuccess(new Elements<>((T[]) new Object[0], true));
  }

  @Override
  public Completes<Elements<T>> next(final long index) {
    return next();
  }

  @Override
  public Completes<Boolean> isSlow() {
    return Completes.withSuccess(slowIterable);
  }
}
