package io.vlingo.xoom.reactivestreams.implnative;

import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.actors.Protocols;
import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.reactivestreams.ControlledSubscription;
import io.vlingo.xoom.reactivestreams.PublisherConfiguration;
import io.vlingo.xoom.reactivestreams.Source;
import io.vlingo.xoom.reactivestreams.StreamPublisher;
import io.vlingo.xoom.reactivestreams.Streams.OverflowPolicy;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.reactivestreams.Publisher;

public final class NativeBuildEntryPoint {
  @CEntryPoint(name = "Java_io_vlingo_xoom_reactivestreamsnative_Native_start")
  public static int start(@CEntryPoint.IsolateThreadContext long isolateId, CCharPointer name) {
    final String nameString = CTypeConversion.toJavaString(name);
    World world = World.startWithDefaults(nameString);

    PublisherConfiguration configuration = new PublisherConfiguration(5, OverflowPolicy.DropHead);

    final Definition definition = Definition.has(StreamPublisher.class, Definition.parameters(Source.only("1", "2", "3"), configuration));

    final Protocols protocols = world.actorFor(new Class[]{Publisher.class, ControlledSubscription.class}, definition);

    Publisher<String> publisher = protocols.get(0);

    ControlledSubscription<String> controlledSubscription = protocols.get(1);
    return 0;
  }
}