package io.vlingo.reactivestreams;

import io.vlingo.actors.World;
import io.vlingo.common.Completes;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.tck.IdentityProcessorVerification;
import org.reactivestreams.tck.TestEnvironment;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StreamProcessorCompatibilityTest extends IdentityProcessorVerification<Integer> {

  public static final long DEFAULT_TIMEOUT_MILLIS = 300L;
  public static final long PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS = 1000L;

  private final World world = World.startWithDefaults("streams");


  public StreamProcessorCompatibilityTest() {
    super(new TestEnvironment(DEFAULT_TIMEOUT_MILLIS), PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS);
  }

  @Override
  public ExecutorService publisherExecutorService() {
    return Executors.newFixedThreadPool(4);
  }

  @Override
  public Integer createElement(int element) {
    return element;
  }

  @Override
  public Processor<Integer, Integer> createIdentityProcessor(int bufferSize) {
    Transformer<Integer, Integer> transformer = Completes::withSuccess;
    return world.actorFor(Processor.class, StreamProcessor.class,
        transformer, bufferSize, PublisherConfiguration.defaultDropHead());
  }

  @Override
  public Publisher<Integer> createFailedPublisher() {
    return s -> s.onError(new RuntimeException("Can't subscribe subscriber: " + s + ", because of reasons."));
  }

  @Override
  public long maxElementsFromPublisher() {
    return super.maxElementsFromPublisher();
  }

  @Override
  public long boundedDepthOfOnNextAndRequestRecursion() {
    return super.boundedDepthOfOnNextAndRequestRecursion();
  }
}
