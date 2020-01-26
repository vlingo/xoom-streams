// Copyright © 2012-2019 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import io.vlingo.common.Completes;

public class RandomNumberOfElementsSource implements Source<String> {
  private final AtomicInteger element = new AtomicInteger(0);
  private final Random count = new Random();
  private final boolean slow;
  private final int total;

  public RandomNumberOfElementsSource(final int total) {
    this(total, false);
  }

  public RandomNumberOfElementsSource(final int total, final boolean slow) {
    this.total = total;
    this.slow = slow;
  }

  @Override
  public Completes<Elements<String>> next() {
    final int current = element.get();
    if (current >= total) {
      return Completes.withSuccess(new Elements<>(new String[0], true));
    }
    final String[] next = randomNumberOfElements(current);
    for (int idx = 0; idx < next.length; ++idx) {
      next[idx] = String.valueOf(element.incrementAndGet());
    }
    return Completes.withSuccess(new Elements<>(next, false));
  }

  @Override
  public Completes<Elements<String>> next(final int maximumElements) {
    return next();
  }

  @Override
  public Completes<Elements<String>> next(final long index) {
    return next();
  }

  @Override
  public Completes<Elements<String>> next(final long index, final int maximumElements) {
    return next();
  }

  @Override
  public Completes<Boolean> isSlow() {
    return Completes.withSuccess(slow);
  }

  private String[] randomNumberOfElements(final int current) {
    final int bound = (current > total - 10 && current < total) ? (total - current) : 7;
    final int number = count.nextInt(bound);
    final String[] next = new String[number <= 0 ? 1 : number];
    return next;
  }
}
