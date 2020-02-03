package org.reactivestreams;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;
import io.vlingo.common.SerializableConsumer;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Processor__Proxy<T, R> implements org.reactivestreams.Processor<T, R> {

  private static final String onSubscribeRepresentation1 = "onSubscribe(org.reactivestreams.Subscription)";
  private static final String onNextRepresentation2 = "onNext(T)";
  private static final String onErrorRepresentation3 = "onError(java.lang.Throwable)";
  private static final String onCompleteRepresentation4 = "onComplete()";
  private static final String subscribeRepresentation5 = "subscribe(org.reactivestreams.Subscriber<? super T>)";

  private final Actor actor;
  private final Mailbox mailbox;

  public Processor__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @Override
  public void onSubscribe(org.reactivestreams.Subscription arg0) {
    if (arg0 == null) throw new NullPointerException("Subscription must not be null.");
    if (!actor.isStopped()) {
      final SerializableConsumer<Processor> consumer = (actor) -> actor.onSubscribe(arg0);
      if (mailbox.isPreallocated()) { mailbox.send(actor, Processor.class, consumer, null, onSubscribeRepresentation1); }
      else { mailbox.send(new LocalMessage<Processor>(actor, Processor.class, consumer, onSubscribeRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, onSubscribeRepresentation1));
    }
  }
  @Override
  public void onNext(T arg0) {
    if (arg0 == null) throw new NullPointerException("Element must not be null.");
    if (!actor.isStopped()) {
      final SerializableConsumer<Processor> consumer = (actor) -> actor.onNext(arg0);
      if (mailbox.isPreallocated()) { mailbox.send(actor, Processor.class, consumer, null, onNextRepresentation2); }
      else { mailbox.send(new LocalMessage<Processor>(actor, Processor.class, consumer, onNextRepresentation2)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, onNextRepresentation2));
    }
  }
  @Override
  public void onError(java.lang.Throwable arg0) {
    if (arg0 == null) throw new NullPointerException("Exception must not be null.");
    if (!actor.isStopped()) {
      final SerializableConsumer<Processor> consumer = (actor) -> actor.onError(arg0);
      if (mailbox.isPreallocated()) { mailbox.send(actor, Processor.class, consumer, null, onErrorRepresentation3); }
      else { mailbox.send(new LocalMessage<Processor>(actor, Processor.class, consumer, onErrorRepresentation3)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, onErrorRepresentation3));
    }
  }
  @Override
  public void onComplete() {
    if (!actor.isStopped()) {
      final SerializableConsumer<Processor> consumer = (actor) -> actor.onComplete();
      if (mailbox.isPreallocated()) { mailbox.send(actor, Processor.class, consumer, null, onCompleteRepresentation4); }
      else { mailbox.send(new LocalMessage<Processor>(actor, Processor.class, consumer, onCompleteRepresentation4)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, onCompleteRepresentation4));
    }
  }
  @Override
  public void subscribe(org.reactivestreams.Subscriber<? super R> arg0) {
    if (arg0 == null) throw new NullPointerException("Subscriber must not be null.");
    if (!actor.isStopped()) {
      final SerializableConsumer<Processor> consumer = (actor) -> actor.subscribe(arg0);
      if (mailbox.isPreallocated()) { mailbox.send(actor, Processor.class, consumer, null, subscribeRepresentation5); }
      else { mailbox.send(new LocalMessage<Processor>(actor, Processor.class, consumer, subscribeRepresentation5)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, subscribeRepresentation5));
    }
  }
}
