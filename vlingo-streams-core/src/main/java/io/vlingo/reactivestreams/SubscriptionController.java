// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

final class SubscriptionController<T> implements Subscription {
  static final AtomicInteger nextId = new AtomicInteger(0);

  private final Queue<T> buffer;
  private final PublisherConfiguration configuration;
  private final int id;
  private int dropIndex;
  private final Subscriber<? super T> subscriber;
  private final ControlledSubscription<T> subscription;

  private boolean cancelled;
  private long count;
  private long maximum;

  SubscriptionController(final Subscriber<? super T> subscriber, final ControlledSubscription<T> subscription, final PublisherConfiguration configuration) {
    this.subscriber = subscriber;
    this.subscription = subscription;
    this.configuration = configuration;
    this.id = nextId.incrementAndGet();
    this.buffer = new ArrayDeque<>();
    this.cancelled = false;
  }

  @Override
  public void cancel() {
    subscription.cancel(this);
  }

  @Override
  public void request(final long maximum) {
    if (maximum <= 0) {
      // System.out.println("REQUEST: " + maximum);
      final IllegalArgumentException e = new IllegalArgumentException("Must be >= 1 and <= Long.MAX_VALUE");
      subscriber.onError(e);
      return;
    }

    subscription.request(this, maximum);
  }

  @Override
  public int hashCode() {
    return 31 * Integer.hashCode(id);
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean equals(final Object other) {
    if (this == other) {
      return true;
    }

    if (other == null || getClass() != other.getClass()) {
      return false;
    }

    return id == (((SubscriptionController<T>) other).id);
  }

  @Override
  public String toString() {
    return "SubscriptionController [id=" + id + " count=" + count + " maximum=" + maximum + " remaining=" + remaining() + " unbounded=" + unbounded() + "]";
  }

  int id() {
    return id;
  }

  final Subscriber<? super T> subscriber() {
    return subscriber;
  }

  //===================================
  // Publish
  //===================================

  boolean hasBufferedElements() {
    return !buffer.isEmpty();
  }

  void onNext(final T element) {
    // System.out.println("ON NEXT TOP REMAINING: " + remaining());
    if (remaining() > 0) {
      // System.out.println("ON NEXT SENDING: " + element);
      sendNext(element);
    } else if (element == null) {
      // System.out.println("NO REMAINING: " + element);
      return;
    } else if (buffer.size() < configuration.bufferSize) {
      // System.out.println("NO REMAINING: BUFFERING: " + element);
      buffer.add(element);
    } else {
      // System.out.println("NO REMAINING: DROPPING: " + element);
      switch (configuration.overflowPolicy) {
      case DropHead:
        dropHeadFor(element);
        break;
      case DropTail:
        dropTailFor(element);
        break;
      case DropCurrent:
        // ignore element
        break;
      }
    }
  }

  void onError(final Throwable cause) {
    subscriber.onError(cause);
  }

  private void dropHeadFor(final T element) {
    buffer.poll();
    buffer.add(element);
  }

  private void dropTailFor(final T element) {
    dropIndex = 0;
    final int lastElement = buffer.size() - 1;
    buffer.removeIf((e) -> dropIndex++ == lastElement);
    buffer.add(element);
  }

  private void sendNext(final T element) {
    // System.out.println("REMAINING: " + remaining());
    long throttleCount = throttleCount();
    // System.out.println("THROTTLE: " + throttleCount);
    T currentElement = element;
    while (throttleCount-- > 0) {
      final T next = swapBufferedOrElse(currentElement);
      if (next != null) {
        // System.out.println("SENDING: " + next);
        currentElement = null;
        subscriber.onNext(next);
        increment();
      } else {
        break;
      }
    }
  }

  private T swapBufferedOrElse(final T element) {
    if (buffer.isEmpty()) {
      return element;
    }

    final T next = buffer.poll();

    if (element != null) {
      buffer.add(element);
    }

    return next;
  }

  //===================================
  // Back pressure
  //===================================

  long accumulate(final long amount) {
    if (maximum < Long.MAX_VALUE) {
      long accumulated = maximum + amount;
      if (accumulated < 0) {
        accumulated = Long.MAX_VALUE;
      }
      // System.out.println("ACCUMULATE: CURRENT MAXIMUM: " + maximum + " AMOUNT: " + amount + " ACCUMULATED: " + accumulated);
      return accumulated;
    }
    return maximum;
  }

  void cancelSubscription() {
    this.cancelled = true;
    this.count = 0;
    this.maximum = 0;
  }

  long count() {
    if (maximum == Long.MAX_VALUE) {
      return maximum;
    }
    return count;
  }

  void increment() {
    if (maximum < Long.MAX_VALUE) {
      ++count;
    }
  }

  long maximum() {
    return maximum;
  }

  long remaining() {
    if (cancelled) {
      return 0;
    }
    return maximum - count;
  }

  void requestFlow(final long maximum) {
    // System.out.println("REQUEST FLOW: " + maximum);

    // NOTE: Since accumulate() works as follows:
    //    long accumulated = maximum + amount;
    // it means that resetting count must NOT be done:
    //    this.count = 0;

    this.maximum = maximum;
  }

  long throttleCount() {
    return configuration.maxThrottle == Streams.DefaultMaxThrottle ?
            remaining() :
            Math.min(configuration.maxThrottle, remaining());
  }

  boolean unbounded() {
    return maximum == Long.MAX_VALUE;
  }
}
