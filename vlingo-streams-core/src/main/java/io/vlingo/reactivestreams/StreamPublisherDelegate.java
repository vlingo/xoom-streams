// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams;

import java.util.HashMap;
import java.util.Map;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import io.vlingo.actors.Stoppable;
import io.vlingo.common.Cancellable;
import io.vlingo.common.Scheduled;
import io.vlingo.common.Scheduler;

public class StreamPublisherDelegate<T> implements Publisher<T>, ControlledSubscription<T> {
  private Cancellable cancellable;
  private final PublisherConfiguration configuration;
  private boolean slow;
  private final Source<T> source;
  private final Map<Integer,SubscriptionController<T>> subscriptions;

  private final ControlledSubscription<T> controlledSubscription;
  private final Scheduler scheduler;
  private final Scheduled<Void> scheduled;
  private final Stoppable stoppable;

  public StreamPublisherDelegate(
          final Source<T> source,
          final PublisherConfiguration configuration,
          final ControlledSubscription<T> controlledSubscription,
          final Scheduler scheduler,
          final Scheduled<Void> scheduled,
          final Stoppable stoppable) {

    this.source = source;
    this.configuration = configuration;
    this.subscriptions = new HashMap<>(2);
    this.controlledSubscription = controlledSubscription;
    this.scheduler = scheduler;
    this.scheduled = scheduled;
    this.stoppable = stoppable;

    determineIfSlow();
  }

  //===================================
  // Publisher
  //===================================

  @Override
  public void subscribe(final Subscriber<? super T> subscriber) {
    // System.out.println("SUBSCRIBE: " + subscriber);

    schedule(true);

    final SubscriptionController<T> controller = new SubscriptionController<>(subscriber, controlledSubscription, configuration);

    subscriptions.putIfAbsent(controller.id(), controller);

    subscriber.onSubscribe(controller);
  }

  //===================================
  // ControlledSubscription
  //===================================

  @Override
  public void cancel(final SubscriptionController<T> controller) {
    // System.out.println("CANCEL: " + controller);

    controller.cancelSubscription();

    subscriptions.remove(controller.id());
  }

  @Override
  public void request(final SubscriptionController<T> controller, final long maximum) {
    // System.out.println("REQUEST: " + controller + " MAXIMUM: " + maximum);

    controller.requestFlow(controller.accumulate(maximum));

    publish(controller, null); // immediately flush buffered elements, if any
  }

  //===================================
  // Internal implementation
  //===================================

  void processNext() {
    if (subscriptions.isEmpty()) {
      // System.out.println("PROCESS NEXT EMPTY");
      return;
    }
    // System.out.println("SOURCE: " + source);
    try {
      source
        .next()
        .andThen(maybeElements -> {
          if (!maybeElements.terminated) {
            // System.out.println("PROCESS NEXT: ELEMENTS: " + maybeElements.toString());
            publish(maybeElements.values);
            schedule(false);
            return maybeElements;
          } else if (flush()) {
            return maybeElements;
          } else {
            // System.out.println("COMPLETING ALL");
            completeAll();
            stoppable.stop();
          }
          return maybeElements;
        })
        .andFinally();
    } catch (Throwable t) {
      publish(t);
    }
  }

  void publish(final T elementOrNull) {
    subscriptions.values().forEach(controller -> controller.onNext(elementOrNull));
  }

  void publish(final SubscriptionController<T> controller, final T elementOrNull) {
    controller.onNext(elementOrNull);
  }

  void publish(final Throwable cause) {
    subscriptions.values().forEach(controller -> controller.onError(cause));
  }

  void stop() {
    // System.out.println("STOPPING");

    cancellable.cancel();

    completeAll();
  }

  private void completeAll() {
    // System.out.println("COMPLETE ALL");

    subscriptions.values().forEach(controller -> controller.subscriber().onComplete());

    subscriptions.clear();
  }

  private void determineIfSlow() {
    // pure evil; don't try this at home.
    // BTW, it is most likely not a slow
    // operation to determine whether the
    // Source is slow, like intsy tinsy
    // blocking.
    this.slow = source.isSlow().await();
  }

  private boolean flushed;
  private boolean flush() {
    flushed = false;
    subscriptions.values().forEach(controller ->  {
      if (controller.hasBufferedElements()) {
        controller.onNext(null);
        flushed = true;
      }
    });
    return flushed;
  }

  private T[] publish(final T[] maybeElements) {
    if (maybeElements.length > 0) {
      for (int idx = 0; idx < maybeElements.length; ++idx) {
        publish(maybeElements[idx]);
      }
    }

    return maybeElements;
  }

  private void schedule(final boolean isSubscribing) {
    if (slow) {
      // System.out.println("SCHEDULE: " + isSubscribing + " SLOW");
      cancellable = scheduler.scheduleOnce(scheduled, null, 0, configuration.probeInterval);
    } else {
      if (isSubscribing && cancellable == null) {
        // System.out.println("SCHEDULE: " + isSubscribing + " FAST");
        cancellable = scheduler.schedule(scheduled, null, 0, configuration.probeInterval);
      }
    }
  }
}
