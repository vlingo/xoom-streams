package org.reactivestreams;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Publisher__Proxy<T> implements org.reactivestreams.Publisher<T> {

  private static final String subscribeRepresentation1 = "subscribe(org.reactivestreams.Subscriber<? super T>)";

  private final Actor actor;
  private final Mailbox mailbox;

  public Publisher__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @Override
  public void subscribe(org.reactivestreams.Subscriber<? super T> arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Publisher> consumer = (actor) -> actor.subscribe(arg0);
      if (mailbox.isPreallocated()) { mailbox.send(actor, Publisher.class, consumer, null, subscribeRepresentation1); }
      else { mailbox.send(new LocalMessage<Publisher>(actor, Publisher.class, consumer, subscribeRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, subscribeRepresentation1));
    }
  }
}
