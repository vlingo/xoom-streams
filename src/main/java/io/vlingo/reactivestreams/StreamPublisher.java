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

import io.vlingo.actors.Actor;
import io.vlingo.actors.Stoppable;
import io.vlingo.common.Cancellable;
import io.vlingo.common.Scheduled;
import io.vlingo.reactivestreams.Streams.PublisherConfiguration;

public class StreamPublisher<A,B> extends Actor implements Publisher<B>, ControlledSubscription<B>, Scheduled<Void>, Stoppable {
  private static final Object[] none = new Object[] { null };

  private Cancellable cancellable;
  private final PublisherConfiguration configuration;
  private final ControlledSubscription<B> controlledSubscription;
  private final Scheduled<Void> scheduled;
  private boolean slow;
  private final Source<A> source;
  private final Map<Integer,SubscriptionController<B>> subscriptions;

  @SuppressWarnings("unchecked")
  public StreamPublisher(final Source<A> source, final PublisherConfiguration configuration) {
    this.source = source;
    this.configuration = configuration;
    this.subscriptions = new HashMap<>(2);
    this.controlledSubscription = selfAs(ControlledSubscription.class);
    this.scheduled = selfAs(Scheduled.class);

    determineIfSlow();
  }

  //===================================
  // Publisher
  //===================================

  @Override
  public void subscribe(final Subscriber<? super B> subscriber) {
    schedule(true);

    final SubscriptionController<B> controller = new SubscriptionController<>(subscriber, controlledSubscription, configuration);

    subscriptions.putIfAbsent(controller.id(), controller);

    subscriber.onSubscribe(controller);
  }

  //===================================
  // ControlledSubscription
  //===================================

  @Override
  public void cancel(final SubscriptionController<B> controller) {
    // TODO: sends message/exception?

    // System.out.println("CANCEL: " + controller);

    controller.cancelSubscription();

    subscriptions.remove(controller.id());
  }

  @Override
  public void request(final SubscriptionController<B> controller, final long maximum) {
    controller.requestFlow(maximum);
  }

  //===================================
  // Scheduled
  //===================================

  @Override
  public void intervalSignal(final Scheduled<Void> scheduled, final Void data) {
    if (subscriptions.isEmpty()) {
      return;
    }

    source
      .next()
      .andThen(maybeElements -> {
        if (!maybeElements.terminated) {
          // System.out.println("ELEMENTS: " + maybeElements.toString());
          publish(maybeElements.values);
          schedule(false);
          return maybeElements;
        } else {
          // System.out.println("COMPLETING ALL");
          completeAll();
          selfAs(Stoppable.class).stop();
        }
        return maybeElements;
      })
      .andFinally();
  }

  //===================================
  // Internal implementation
  //===================================

  @Override
  public void stop() {
    // System.out.println("STOPPING");

    cancellable.cancel();

    completeAll();

    super.stop();
  }

  private void completeAll() {
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

  @SuppressWarnings("unchecked")
  private A[] publish(final A[] maybeElements) {
    final A[] elements = maybeElements.length > 0 ? maybeElements : (A[]) none;

    for (int idx = 0; idx < elements.length; ++idx) {
      publish(maybeElements[idx]);
    }

    return maybeElements;
  }

  private void publish(final A elementOrNull) {
    subscriptions.values().forEach(controller -> controller.onNext(toB(elementOrNull)));
  }

  private void schedule(final boolean isSubscribing) {
    if (slow) {
      cancellable = scheduler().scheduleOnce(scheduled, null, 0, configuration.probeInterval);
    } else {
      if (isSubscribing && cancellable == null) {
        cancellable = scheduler().schedule(scheduled, null, 0, configuration.probeInterval);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private B toB(final A element) {
    // System.out.println("TO B: " + element);
    return (B) element;
  }
}
