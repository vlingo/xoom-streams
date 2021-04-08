// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.reactivestreams;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.actors.Protocols;
import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.reactivestreams.Streams.OverflowPolicy;

public abstract class StreamPubSubTest {
  protected PublisherConfiguration configuration;
  protected ControlledSubscription<String> controlledSubscription;
  protected Publisher<String> publisher;
  protected Source<String> sourceOf123;
  protected Source<String> sourceOfABC;
  protected Source<String> sourceRandomNumberOfElements;
  protected Subscriber<String> subscriber;
  protected World world;

  @Before
  public void setUp() {
    world = World.startWithDefaults("streams");

    configuration = new PublisherConfiguration(5, OverflowPolicy.DropHead);

    sourceOf123 = Source.only("1", "2", "3");

    sourceOfABC = Source.only("A", "B", "C");

    sourceRandomNumberOfElements = new RandomNumberOfElementsSource(100);
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  protected void createPublisherWith(final Source<String> source) {
    final Definition definition = Definition.has(StreamPublisher.class, Definition.parameters(source, configuration));

    final Protocols protocols = world.actorFor(new Class[] { Publisher.class, ControlledSubscription.class }, definition);

    publisher = protocols.get(0);

    controlledSubscription = protocols.get(1);
  }

  @SuppressWarnings("unchecked")
  protected <T> void createSubscriberWith(final Sink<T> sink, final long requestThreshold) {
    subscriber = world.actorFor(Subscriber.class, StreamSubscriber.class, sink, requestThreshold);

    publisher.subscribe(subscriber);
  }

  @SuppressWarnings("unchecked")
  protected <T>Subscriber<T> createSubscriberWithoutSubscribing(final Sink<T> sink, final long requestThreshold) {
    final Subscriber<T> subscriber = world.actorFor(Subscriber.class, StreamSubscriber.class, sink, requestThreshold);
    return subscriber;
  }

  protected List<Integer> integerListOf1To(final int n) {
    final List<Integer> list = new ArrayList<>();
    for (int idx = 1; idx <= n; ++idx) {
      list.add(idx);
    }
    return list;
  }

  protected List<String> stringListOf1To(final int n) {
    final List<String> list = new ArrayList<>();
    for (int idx = 1; idx <= n; ++idx) {
      list.add(String.valueOf(idx));
    }
    return list;
  }
}
