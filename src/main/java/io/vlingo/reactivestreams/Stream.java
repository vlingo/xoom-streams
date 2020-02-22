package io.vlingo.reactivestreams;

/**
 * A {@code Stream} protocol implemented by the publisher side and
 * started by the subscribing side using a {@code Sink<S>} as inflow.
 */
public interface Stream {
  /** The default rate of flow bursts. */
  public static final long DefaultFlowRate = 100;

  /** The default interval to poll the {@code Source}. */
  public static final int DefaultProbeInterval = PublisherConfiguration.DefaultProbeInterval;

  /** The fast interval to poll the {@code Source}. */
  public static int FastProbeInterval = PublisherConfiguration.FastProbeInterval;

  /** The fastest interval to poll the {@code Source}. */
  public static int FastestProbeInterval = PublisherConfiguration.FastestProbeInterval;

  /**
   * Potentially changes the underlying {@code Subscriber}'s {@code flowElementsRate}
   * if its current is not equal to the {@code flowElementsRate}.
   * @param flowElementsRate the long rate at which new elements will flow into the {@code Sink<S>}
   */
  void request(final long flowElementsRate);

  /**
   * Starts the flow from {@code Publisher<S>} to {@code Subscriber<S>}, which in turn flows the
   * elements to the {@code sink}. The {@code DefaultFlowRate} and {@code DefaultProbeInterval}
   * are used.
   * @param sink the {@code Sink<S>} to which the {@code Subscriber<S>} pushes elements
   * @param <S> the type of the elements that the Sink intakes
   */
  <S> void flowInto(final Sink<S> sink);

  /**
   * Starts the flow from {@code Publisher<S>} to {@code Subscriber<S>} at the rate of
   * {@code flowElementsRate}, which in turn flows the elements to the {@code sink}.
   * The {@code DefaultProbeInterval} is used.
   * @param sink the {@code Sink<S>} to which the {@code Subscriber<S>} pushes elements
   * @param flowElementsRate the long limit of elements to push to the Sink at any one time
   * @param <S> the type of the elements that the Sink intakes
   */
  <S> void flowInto(final Sink<S> sink, final long flowElementsRate);

  /**
   * Starts the flow from {@code Publisher<S>} to {@code Subscriber<S>} at the rate of
   * {@code flowElementsRate}, which in turn flows the elements to the {@code sink}.
   * The {@code Publisher<S>} will poll for new elements on every {@code probeInterval}.
   * @param sink the {@code Sink<S>} to which the {@code Subscriber<S>} pushes elements
   * @param flowElementsRate the long limit of elements to push to the Sink at any one time
   * @param probeInterval the int indicating how often the {@code Publisher<S>} should poll its {@code Source<S>}
   * @param <S> the type of the elements that the Sink intakes
   */
  <S> void flowInto(final Sink<S> sink, final long flowElementsRate, final int probeInterval);

  /**
   * Sends a message to the {@code Publisher<S>} to stop polling elements from its
   * {@code Source<S>} and pushing them to the {@code Sink<S>} by way of the {@code Subscriber<S>}.
   * Note that some elements may have already been pushed to the {@code Subscriber<S>} but
   * have not yet fully flowed to the {@code Sink<S>}. This means you cannot assume that
   * flow will stop immediately.
   */
  void stop();
}
