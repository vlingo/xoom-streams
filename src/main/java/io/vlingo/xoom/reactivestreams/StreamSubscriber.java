// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.reactivestreams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.actors.Stoppable;

/**
 * The standard {@code Subscriber<T>} of streams.
 *
 * @param <T> the type of values consumed
 */
public class StreamSubscriber<T> extends Actor implements Subscriber<T>, Stoppable {
  private final Subscriber<T> subscriber;

  public StreamSubscriber(final Sink<T> sink, final long requestThreshold) {
    this.subscriber = new StreamSubscriberDelegate<>(sink, requestThreshold, logger());
  }

  @Override
  public void onSubscribe(final Subscription subscription) {
    subscriber.onSubscribe(subscription);
  }

  @Override
  public void onNext(final T value) {
    subscriber.onNext(value);
  }

  @Override
  public void onComplete() {
    this.subscriber.onComplete();
  }

  @Override
  public void onError(final Throwable cause) {
    subscriber.onError(cause);
  }
}
