package org.reactivestreams;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;
import io.vlingo.common.SerializableConsumer;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Subscriber__Proxy<T> implements org.reactivestreams.Subscriber<T> {

  private static final String onSubscribeRepresentation1 = "onSubscribe(org.reactivestreams.Subscription)";
  private static final String onCompleteRepresentation2 = "onComplete()";
  private static final String onErrorRepresentation3 = "onError(java.lang.Throwable)";
  private static final String onNextRepresentation4 = "onNext(T)";

  private final Actor actor;
  private final Mailbox mailbox;

  public Subscriber__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @Override
  public void onSubscribe(org.reactivestreams.Subscription arg0) {
    if (arg0 == null) throw new NullPointerException("Subscription must not be null.");
    if (!actor.isStopped()) {
      final SerializableConsumer<Subscriber> consumer = (actor) -> actor.onSubscribe(arg0);
      if (mailbox.isPreallocated()) { mailbox.send(actor, Subscriber.class, consumer, null, onSubscribeRepresentation1); }
      else { mailbox.send(new LocalMessage<Subscriber>(actor, Subscriber.class, consumer, onSubscribeRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, onSubscribeRepresentation1));
    }
  }
  @Override
  public void onComplete() {
    if (!actor.isStopped()) {
      final SerializableConsumer<Subscriber> consumer = (actor) -> actor.onComplete();
      if (mailbox.isPreallocated()) { mailbox.send(actor, Subscriber.class, consumer, null, onCompleteRepresentation2); }
      else { mailbox.send(new LocalMessage<Subscriber>(actor, Subscriber.class, consumer, onCompleteRepresentation2)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, onCompleteRepresentation2));
    }
  }
  @Override
  public void onError(java.lang.Throwable arg0) {
    if (arg0 == null) throw new NullPointerException("Exception must not be null.");
    if (!actor.isStopped()) {
      final SerializableConsumer<Subscriber> consumer = (actor) -> actor.onError(arg0);
      if (mailbox.isPreallocated()) { mailbox.send(actor, Subscriber.class, consumer, null, onErrorRepresentation3); }
      else { mailbox.send(new LocalMessage<Subscriber>(actor, Subscriber.class, consumer, onErrorRepresentation3)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, onErrorRepresentation3));
    }
  }
  @Override
  public void onNext(T arg0) {
    if (arg0 == null) throw new NullPointerException("Element must not be null.");
    if (!actor.isStopped()) {
      final SerializableConsumer<Subscriber> consumer = (actor) -> actor.onNext(arg0);
      if (mailbox.isPreallocated()) { mailbox.send(actor, Subscriber.class, consumer, null, onNextRepresentation4); }
      else { mailbox.send(new LocalMessage<Subscriber>(actor, Subscriber.class, consumer, onNextRepresentation4)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, onNextRepresentation4));
    }
  }
}
