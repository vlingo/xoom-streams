package io.vlingo.reactivestreams;

import io.vlingo.actors.Definition;
import io.vlingo.actors.Protocols;
import io.vlingo.actors.World;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class StreamPublisherCompatibilityTest extends PublisherVerification<Long> {

  public StreamPublisherCompatibilityTest() {
    super(new TestEnvironment());
  }

  @Override
  public Publisher<Long> createPublisher(long l) {
    World world = World.startWithDefaults("streams");
    Source<Long> source = Source.with(
        LongStream.range(0, l).boxed().collect(Collectors.toList()));
    final Definition definition = Definition.has(StreamPublisher.class,
        Definition.parameters(source, PublisherConfiguration.defaultDropHead()));

    final Protocols protocols = world.actorFor(new Class[] { Publisher.class, ControlledSubscription.class }, definition);

    return protocols.get(0);
  }

  @Override
  public Publisher<Long> createFailedPublisher() {
    return s ->
        s.onError(new RuntimeException("Can't subscribe subscriber: " + s + ", because of reasons."));
  }

  @Override
  public long maxElementsFromPublisher() {
    return 1000;
  }
}
