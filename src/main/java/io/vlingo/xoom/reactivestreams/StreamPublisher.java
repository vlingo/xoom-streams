// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.reactivestreams;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.actors.Stoppable;
import io.vlingo.xoom.common.Scheduled;

public class StreamPublisher<T> extends Actor implements Publisher<T>, ControlledSubscription<T>, Scheduled<Void>, Stoppable {
  private final StreamPublisherDelegate<T> delegate;

  @SuppressWarnings("unchecked")
  public StreamPublisher(final Source<T> source, final PublisherConfiguration configuration) {
    this.delegate = new StreamPublisherDelegate<>(source, configuration, selfAs(ControlledSubscription.class), scheduler(), selfAs(Scheduled.class), selfAs(Stoppable.class));
  }

  //===================================
  // Publisher
  //===================================

  @Override
  public void subscribe(final Subscriber<? super T> subscriber) {
    if (subscriber != null) {
      delegate.subscribe(subscriber);
    }
  }

  //===================================
  // ControlledSubscription
  //===================================

  @Override
  public void cancel(final SubscriptionController<T> controller) {
    delegate.cancel(controller);
  }

  @Override
  public void request(final SubscriptionController<T> controller, final long maximum) {
    delegate.request(controller, maximum);
  }

  //===================================
  // Scheduled
  //===================================

  @Override
  public void intervalSignal(final Scheduled<Void> scheduled, final Void data) {
    delegate.processNext();
  }

  //===================================
  // Internal implementation
  //===================================

  @Override
  public void stop() {
    delegate.stop();

    super.stop();
  }
}
