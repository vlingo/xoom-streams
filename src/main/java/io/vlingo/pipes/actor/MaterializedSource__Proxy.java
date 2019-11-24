package io.vlingo.pipes.actor;

import io.vlingo.actors.*;
import io.vlingo.common.Completes;

import java.util.Optional;

public class MaterializedSource__Proxy implements MaterializedSource {

  private static final String nextIfAnyRepresentation1 = "nextIfAny()";
  private static final String asSourceRepresentation2 = "asSource()";
  private static final String stopRepresentation3 = "stop()";
  private static final String isStoppedRepresentation4 = "isStopped()";
  private static final String concludeRepresentation5 = "conclude()";

  private final Actor actor;
  private final Mailbox mailbox;

  public MaterializedSource__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  public Completes<Optional<io.vlingo.pipes.Record[]>> nextIfAny() {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<MaterializedSource> consumer = (actor) -> actor.nextIfAny();
      final Completes<Optional<io.vlingo.pipes.Record[]>> returnValue = Completes.using(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, MaterializedSource.class, consumer, Returns.value(returnValue), nextIfAnyRepresentation1); }
      else { mailbox.send(new LocalMessage<MaterializedSource>(actor, MaterializedSource.class, consumer, Returns.value(returnValue), nextIfAnyRepresentation1)); }
      return returnValue;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, nextIfAnyRepresentation1));
    }
    return null;
  }
  public Completes<MaterializedSource> asSource() {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<MaterializedSource> consumer = (actor) -> actor.asSource();
      final Completes<MaterializedSource> returnValue = Completes.using(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, MaterializedSource.class, consumer, Returns.value(returnValue), asSourceRepresentation2); }
      else { mailbox.send(new LocalMessage<MaterializedSource>(actor, MaterializedSource.class, consumer, Returns.value(returnValue), asSourceRepresentation2)); }
      return returnValue;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, asSourceRepresentation2));
    }
    return null;
  }
  public void stop() {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<MaterializedSource> consumer = (actor) -> actor.stop();
      if (mailbox.isPreallocated()) { mailbox.send(actor, MaterializedSource.class, consumer, null, stopRepresentation3); }
      else { mailbox.send(new LocalMessage<MaterializedSource>(actor, MaterializedSource.class, consumer, stopRepresentation3)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, stopRepresentation3));
    }
  }
  public boolean isStopped() {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<MaterializedSource> consumer = (actor) -> actor.isStopped();
      if (mailbox.isPreallocated()) { mailbox.send(actor, MaterializedSource.class, consumer, null, isStoppedRepresentation4); }
      else { mailbox.send(new LocalMessage<MaterializedSource>(actor, MaterializedSource.class, consumer, isStoppedRepresentation4)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, isStoppedRepresentation4));
    }
    return false;
  }
  public void conclude() {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<MaterializedSource> consumer = (actor) -> actor.conclude();
      if (mailbox.isPreallocated()) { mailbox.send(actor, MaterializedSource.class, consumer, null, concludeRepresentation5); }
      else { mailbox.send(new LocalMessage<MaterializedSource>(actor, MaterializedSource.class, consumer, concludeRepresentation5)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, concludeRepresentation5));
    }
  }
}
