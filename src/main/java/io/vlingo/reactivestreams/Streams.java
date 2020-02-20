// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import io.vlingo.actors.Logger;
import io.vlingo.actors.Stage;

public class Streams {
  private static Logger logger;

  /**
   * The default total number of elements to buffer
   * internally before applying the {@code OverflowPolicy}.
   */
  public static final int DefaultBufferSize = 256;

  /**
   * The default maximum number of elements to deliver
   * to the {@code Sink} before internally buffering.
   * The request maximum will be honored if the throttle
   * is higher than the remaining number requested. The
   * default of {@code -1} indicates to deliver up to the
   * remaining number of elements, up to the request maximum.
   */
  public static final int DefaultMaxThrottle = -1;

  /**
   * Declares what the {@code Publisher} should do in the case
   * of the internal buffer reaching overflow of elements.
   */
  public static enum OverflowPolicy {
    /**
     * Drops the head (first) element to make room for appending current as the tail.
     */
    DropHead,

    /**
     * Drops the tail (last) element to make room for appending current as the tail.
     */
    DropTail,

    /**
     * Drops the current element in favor of delivering the total number
     * of previously buffered elements.
     */
    DropCurrent
  }

  /**
   * Sets a {@code Logger} for use by streams.
   * @param logger Logger
   */
  public static void logger(final Logger logger) {
    if (Streams.logger != null) {
      throw new IllegalStateException("Logger is already set.");
    }
    Streams.logger = logger;
  }

  /**
   * Answer the {@code Logger} that may be used by streams.
   * @return Logger
   * @throws NullPointerException if the logger is null
   */
  public static Logger logger() {
    if (logger == null) {
      throw new NullPointerException("Logger is null.");
    }
    return logger;
  }

  /**
   * Answer a new {@code Publisher<T>} created within {@code stage} using {@code source} from
   * which to read elements. The default {@code PublisherConfiguration#defaultDropHead()} is used.
   * @param stage the Stage within with the {@code Publisher<T>} actor is created
   * @param source the {@code Source<S>} used by the {@code Publisher<T>} to read elements
   * @param <T> the T typed elements to be published
   * @param <S> the S typed elements provided by the Source
   * @return {@code Publisher<T>}
   */
  static <T,S> Publisher<T> publisherWith(final Stage stage, final Source<S> source) {
    return publisherWith(stage, source, PublisherConfiguration.defaultDropHead());
  }

  /**
   * Answer a new {@code Publisher<T>} created within {@code stage}, reading from {@code source},
   * and configured by the {@code configuration}.
   * @param stage the Stage within with the {@code Publisher<T>} actor is created
   * @param source the {@code Source<S>} used by the {@code Publisher<T>} to read elements
   * @param configuration the PublisherConfiguration used to configure the {@code Publisher<T>}
   * @param <T> the T typed elements to be published
   * @param <S> the S typed elements provided by the Source
   * @return {@code Publisher<T>}
   */
  @SuppressWarnings("unchecked")
  static <T,S> Publisher<T> publisherWith(final Stage stage, final Source<S> source, final PublisherConfiguration configuration) {
    return stage.actorFor(Publisher.class, StreamPublisher.class, source, configuration);
  }

  /**
   * Answer a new {@code Subscriber<T>} created within {@code stage} and pushing to {@code sink},
   * with an unbounded request threshold.
   * @param stage the Stage within with the {@code Subscriber<T>} actor is created
   * @param sink the {@code Sink<S>} to which elements are pushed
   * @param <T> the T typed elements of the subscription
   * @param <S> the S typed elements provided to the Sink
   * @return {@code Subscriber<T>}
   */
  static <T,S> Subscriber<T> subscriberWith(final Stage stage, final Sink<S> sink) {
    return subscriberWith(stage, sink, Long.MAX_VALUE);
  }

  /**
   * Answer a new {@code Subscriber<T>} created within {@code stage} and pushing to {@code sink},
   * with the {@code requestThreshold}. If the {@code requestThreshold} is less than the
   * unbounded {@code Long.MAX_VALUE}, the same amount is requested each time {@code requestThreshold}
   * elements are streamed to the {@code Subscriber<T>}.
   * @param stage the Stage within with the {@code Subscriber<T>} actor is created
   * @param sink the {@code Sink<S>} to which elements are pushed
   * @param requestThreshold the long request limit to be used until the subscription is completed
   * @param <T> the T typed elements of the subscription
   * @param <S> the S typed elements provided to the Sink
   * @return {@code Subscriber<T>}
   */
  @SuppressWarnings("unchecked")
  static <T,S> Subscriber<T> subscriberWith(final Stage stage, final Sink<S> sink, final long requestThreshold) {
    return stage.actorFor(Subscriber.class, StreamSubscriber.class, sink, requestThreshold);
  }
}
