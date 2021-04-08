// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.reactivestreams;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.vlingo.xoom.actors.testkit.AccessSafely;

public class TestSubscriber<T> implements Subscriber<T> {
  private AccessSafely access;

  private final AtomicInteger onSubscribeCount = new AtomicInteger(0);
  private final AtomicInteger onNextCount = new AtomicInteger(0);
  private final AtomicInteger onErrorCount = new AtomicInteger(0);
  private final AtomicInteger onCompleteCount = new AtomicInteger(0);

  private final List<T> values = new CopyOnWriteArrayList<>();

  private final Sink<T> sink;

  private boolean cancelled = false;
  private final int cancelAfterElements;
  private Subscription subscription;
  private final int total;

  public TestSubscriber(final int total) {
    this(total, total * total);
  }

  public TestSubscriber(final int total, final int cancelAfterElements) {
    this(null, total, cancelAfterElements);
  }

  public TestSubscriber(final Sink<T> sink, final int total, final int cancelAfterElements) {
    this.sink = sink;
    this.total = total;
    this.cancelAfterElements = cancelAfterElements;

    this.access = afterCompleting(0);
  }

  @Override
  public void onSubscribe(final Subscription subscription) {
    if (this.subscription != null) {
      subscription.cancel();
      return;
    }
    this.subscription = subscription;

    // System.out.println("RECEIVED ON SUBSCRIBE: " + subscription);
    access.writeUsing("onSubscribe", 1);
    subscription.request(total);

    if (sink != null) {
      sink.ready();
    }
  }

  @Override
  public void onNext(final T value) {
    // System.out.println("RECEIVED ON NEXT: " + value);
    access.writeUsing("onNext", 1);
    access.writeUsing("values", value);

    if (onNextCount.get() >= cancelAfterElements && !cancelled) {
      subscription.cancel();
      if (sink != null) {
        sink.terminate();
      }
      cancelled = true;
    }

    if (sink != null) {
      sink.whenValue(value);
    }
  }

  @Override
  public void onComplete() {
    // System.out.println("RECEIVED ON COMPLETE");
    access.writeUsing("onComplete", 1);

    if (sink != null) {
      sink.terminate();
    }
  }

  @Override
  public void onError(final Throwable t) {
    // System.out.println("RECEIVED ON ERROR: " + t.getMessage());
    access.writeUsing("onError", 1);

    if (sink != null) {
      sink.terminate();
    }
  }

  public AccessSafely afterCompleting(final int times) {
    access = AccessSafely.afterCompleting(times);

    access.writingWith("onSubscribe", (Integer value) -> onSubscribeCount.addAndGet(value));
    access.writingWith("onNext", (Integer value) -> { /*System.out.println("ADD: " + value);*/ onNextCount.addAndGet(value); });
    access.writingWith("onError", (Integer value) -> onErrorCount.addAndGet(value));
    access.writingWith("onComplete", (Integer value) -> onCompleteCount.addAndGet(value));

    access.writingWith("values", (T value) -> values.add(value));

    access.readingWith("onSubscribe", () -> onSubscribeCount.get());
    access.readingWith("onNext", () -> onNextCount.get());
    access.readingWith("onError", () -> onErrorCount.get());
    access.readingWith("onComplete", () -> onCompleteCount.get());

    access.readingWith("values", () -> values);

    return access;
  }

  public int accessValueMustBe(final String name, final int expected) {
    int current = 0;
    for (int tries = 0; tries < 10; ++tries) {
      final int value = access.readFrom(name);
      if (value >= expected) {
        return value;
      }
      if (current != value) {
        current = value;
        // System.out.println("VALUE: " + value);
      }
      try { Thread.sleep(100); } catch (Exception e) { }
    }
    return expected == 0 ? -1 : 0;
  }
}
