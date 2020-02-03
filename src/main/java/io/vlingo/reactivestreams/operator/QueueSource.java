// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams.operator;

import java.util.ArrayDeque;
import java.util.Queue;

import io.vlingo.common.Completes;
import io.vlingo.reactivestreams.Elements;
import io.vlingo.reactivestreams.Source;

/**
 * A standard {@code Source<R>} that queues values to provide to a {@code Publisher<R>}.
 * @param <R> the type queued and provided
 */
public abstract class QueueSource<R> implements Source<R> {
  private final Queue<R> queue;
  private final boolean slow;
  private boolean terminated;

  protected QueueSource(final boolean slow) {
    this.slow = slow;
    this.queue = new ArrayDeque<>(1);
    this.terminated = false;
  }

  @Override
  public Completes<Elements<R>> next() {
    return next(0, 1);
  }

  @Override
  public Completes<Elements<R>> next(final int maximumElements) {
    return next(0, maximumElements);
  }

  @Override
  public Completes<Elements<R>> next(final long index) {
    return next(0, 1);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Completes<Elements<R>> next(final long index, final int maximumElements) {
    if (!queue.isEmpty()) {
      final int total = Math.min(queue.size(), maximumElements);
      final R[] elements = (R[]) new Object[total];
      for (int idx = 0; idx < total; ++idx) {
        elements[idx] = queue.poll();
      }
      return Completes.withSuccess(Elements.of(elements));
    }
    return Completes.withSuccess(terminated ? Elements.terminated() : Elements.empty());
  }

  @Override
  public Completes<Boolean> isSlow() {
    return Completes.withSuccess(slow);
  }

  protected void add(final R value) {
    queue.add(value);
  }

  protected void terminated() {
    terminated = true;
  }

  protected boolean isTerminated() {
    return terminated;
  }
}
