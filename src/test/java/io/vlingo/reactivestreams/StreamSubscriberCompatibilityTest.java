package io.vlingo.reactivestreams;

import io.vlingo.actors.World;
import org.reactivestreams.Subscriber;
import org.reactivestreams.tck.SubscriberBlackboxVerification;
import org.reactivestreams.tck.TestEnvironment;

public class StreamSubscriberCompatibilityTest extends SubscriberBlackboxVerification<Integer> {
  protected StreamSubscriberCompatibilityTest() {
    super(new TestEnvironment());
  }

  @Override
  public Integer createElement(int element) {
    return element;
  }

  @Override
  public Subscriber<Integer> createSubscriber() {
    World world = World.startWithDefaults("streams");
    SafeConsumerSink<Integer> sink = new SafeConsumerSink<>();
    return world.actorFor(Subscriber.class, StreamSubscriber.class, sink, 10);
  }
}
