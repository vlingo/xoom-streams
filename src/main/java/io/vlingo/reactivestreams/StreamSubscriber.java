// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.vlingo.actors.Actor;
import io.vlingo.actors.Stoppable;

/**
 * The standard {@code Subscriber<T>} of streams.
 *
 * @param <T> the type of values consumed
 */
public class StreamSubscriber<T> extends Actor implements Subscriber<T>, Stoppable {
  private boolean completed;
  private long count;
  private boolean errored;
  private final long requestThreshold;
  private final Sink<T> sink;
  private Subscription subscription;

  public StreamSubscriber(final Sink<T> sink, final long requestThreshold) {
    this.sink = sink;
    this.requestThreshold = requestThreshold;
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

    logger().info("Subscriber with " + sink + " is completed.");
  }

  @Override
  public void onError(final Throwable cause) {
    this.errored = true;

    terminate();

    logger().error("Subscriber with " + sink + " is terminating because: " + cause.getMessage(), cause);
  }

  private boolean isFinalized() {
    return completed || errored;
  }

  private void terminate() {
    sink.terminate();
  }
}
