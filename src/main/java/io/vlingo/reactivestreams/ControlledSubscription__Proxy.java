// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;
import io.vlingo.common.SerializableConsumer;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ControlledSubscription__Proxy<T> implements io.vlingo.reactivestreams.ControlledSubscription<T> {

  private static final String requestRepresentation1 = "request(io.vlingo.reactivestreams.SubscriptionController<T>, long)";
  private static final String cancelRepresentation2 = "cancel(io.vlingo.reactivestreams.SubscriptionController<T>)";

  private final Actor actor;
  private final Mailbox mailbox;

  public ControlledSubscription__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @Override
  public void request(io.vlingo.reactivestreams.SubscriptionController<T> arg0, long arg1) {
    if (!actor.isStopped()) {
      final SerializableConsumer<ControlledSubscription> consumer = (actor) -> actor.request(arg0, arg1);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ControlledSubscription.class, consumer, null, requestRepresentation1); }
      else { mailbox.send(new LocalMessage<ControlledSubscription>(actor, ControlledSubscription.class, consumer, requestRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, requestRepresentation1));
    }
  }
  @Override
  public void cancel(io.vlingo.reactivestreams.SubscriptionController<T> arg0) {
    if (!actor.isStopped()) {
      final SerializableConsumer<ControlledSubscription> consumer = (actor) -> actor.cancel(arg0);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ControlledSubscription.class, consumer, null, cancelRepresentation2); }
      else { mailbox.send(new LocalMessage<ControlledSubscription>(actor, ControlledSubscription.class, consumer, cancelRepresentation2)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, cancelRepresentation2));
    }
  }
}
