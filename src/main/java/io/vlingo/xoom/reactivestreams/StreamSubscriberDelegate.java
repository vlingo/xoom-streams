// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.reactivestreams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.vlingo.xoom.actors.Logger;

public class StreamSubscriberDelegate<T> implements Subscriber<T> {
  private boolean completed;
  private long count;
  private boolean errored;
  private final Logger logger;
  private final long requestThreshold;
  private final Sink<T> sink;
  private Subscription subscription;

  public StreamSubscriberDelegate(final Sink<T> sink, final long requestThreshold, final Logger logger) {
    this.sink = sink;
    this.requestThreshold = requestThreshold;
    this.logger = logger;
    this.count = 0;
    this.completed = false;
    this.errored = false;
  }

  @Override
  public void onSubscribe(final Subscription subscription) {
    if (this.subscription == null) {
      this.subscription = subscription;
      this.sink.ready();
      this.subscription.request(requestThreshold);
    } else {
      subscription.cancel();
    }
  }

  @Override
  public void onNext(final T value) {
    if (!isFinalized()) {
      sink.whenValue(value);

      if (++count >= requestThreshold) {
        subscription.request(requestThreshold);
      }
    }
  }

  @Override
  public void onComplete() {
    this.completed = true;

    terminate();

    logger.info("Subscriber with " + sink + " is completed.");
  }

  @Override
  public void onError(final Throwable cause) {
    this.errored = true;

    terminate();

    logger.error("Subscriber with " + sink + " is terminating because: " + cause.getMessage(), cause);
  }

  public boolean isFinalized() {
    return completed || errored;
  }

  public void cancelSubscription() {
    subscription.cancel();
  }

  private void terminate() {
    sink.terminate();
  }
}
