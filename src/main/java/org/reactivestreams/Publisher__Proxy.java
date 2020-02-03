package org.reactivestreams;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;
import io.vlingo.common.SerializableConsumer;

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
    if (arg0 == null) throw new NullPointerException("Subscriber must not be null.");
    if (!actor.isStopped()) {
      final SerializableConsumer<Publisher> consumer = (actor) -> actor.subscribe(arg0);
      if (mailbox.isPreallocated()) { mailbox.send(actor, Publisher.class, consumer, null, subscribeRepresentation1); }
      else { mailbox.send(new LocalMessage<Publisher>(actor, Publisher.class, consumer, subscribeRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, subscribeRepresentation1));
    }
  }
}
