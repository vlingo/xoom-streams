package io.vlingo.reactivestreams;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;

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
      final java.util.function.Consumer<ControlledSubscription> consumer = (actor) -> actor.request(arg0, arg1);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ControlledSubscription.class, consumer, null, requestRepresentation1); }
      else { mailbox.send(new LocalMessage<ControlledSubscription>(actor, ControlledSubscription.class, consumer, requestRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, requestRepresentation1));
    }
  }
  @Override
  public void cancel(io.vlingo.reactivestreams.SubscriptionController<T> arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ControlledSubscription> consumer = (actor) -> actor.cancel(arg0);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ControlledSubscription.class, consumer, null, cancelRepresentation2); }
      else { mailbox.send(new LocalMessage<ControlledSubscription>(actor, ControlledSubscription.class, consumer, cancelRepresentation2)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, cancelRepresentation2));
    }
  }
}
