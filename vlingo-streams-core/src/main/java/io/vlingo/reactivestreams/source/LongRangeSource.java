// Copyright © 2012-2019 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams.source;

import io.vlingo.common.Completes;
import io.vlingo.reactivestreams.Elements;
import io.vlingo.reactivestreams.Source;

public class LongRangeSource implements Source<Long> {
  private long current;
  private final long endExclusive;
  private final long startInclusive;

  public LongRangeSource(final long startInclusive, final long endExclusive) {
    this.startInclusive = startInclusive;
    this.endExclusive = endExclusive;

    this.current = startInclusive;
  }

  @Override
  public Completes<Elements<Long>> next() {
    if (current < endExclusive) {
      final Long[] element = new Long[1];
      element[0] = current++;
      return Completes.withSuccess(new Elements<>(element, false));
    }
    return Completes.withSuccess(new Elements<>(new Long[0], true));
  }

  @Override
  public Completes<Elements<Long>> next(long index) {
    return next();
  }

  @Override
  public Completes<Boolean> isSlow() {
    return Completes.withSuccess(false);
  }

  @Override
  public String toString() {
    return "LongRangeSource [startInclusive=" + startInclusive +
              " endExclusive=" + endExclusive + " current=" + current + "]";
  }
}
