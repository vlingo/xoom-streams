package io.vlingo.pipes.actor;

import io.vlingo.actors.*;
import io.vlingo.common.Completes;

public class Materialized__Proxy implements Materialized {

  private static final String asSourceRepresentation1 = "asSource()";
  private static final String stopRepresentation2 = "stop()";
  private static final String isStoppedRepresentation3 = "isStopped()";
  private static final String concludeRepresentation4 = "conclude()";

  private final Actor actor;
  private final Mailbox mailbox;

  public Materialized__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  public Completes<MaterializedSource> asSource() {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Materialized> consumer = (actor) -> actor.asSource();
      final Completes<MaterializedSource> returnValue = Completes.using(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, Materialized.class, consumer, Returns.value(returnValue), asSourceRepresentation1); }
      else { mailbox.send(new LocalMessage<Materialized>(actor, Materialized.class, consumer, Returns.value(returnValue), asSourceRepresentation1)); }
      return returnValue;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, asSourceRepresentation1));
    }
    return null;
  }
  public void stop() {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Materialized> consumer = (actor) -> actor.stop();
      if (mailbox.isPreallocated()) { mailbox.send(actor, Materialized.class, consumer, null, stopRepresentation2); }
      else { mailbox.send(new LocalMessage<Materialized>(actor, Materialized.class, consumer, stopRepresentation2)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, stopRepresentation2));
    }
  }
  public boolean isStopped() {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Materialized> consumer = (actor) -> actor.isStopped();
      if (mailbox.isPreallocated()) { mailbox.send(actor, Materialized.class, consumer, null, isStoppedRepresentation3); }
      else { mailbox.send(new LocalMessage<Materialized>(actor, Materialized.class, consumer, isStoppedRepresentation3)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, isStoppedRepresentation3));
    }
    return false;
  }
  public void conclude() {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Materialized> consumer = (actor) -> actor.conclude();
      if (mailbox.isPreallocated()) { mailbox.send(actor, Materialized.class, consumer, null, concludeRepresentation4); }
      else { mailbox.send(new LocalMessage<Materialized>(actor, Materialized.class, consumer, concludeRepresentation4)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, concludeRepresentation4));
    }
  }
}
