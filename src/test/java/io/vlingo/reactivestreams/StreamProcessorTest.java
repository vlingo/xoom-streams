// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;

import io.vlingo.actors.testkit.AccessSafely;

@SuppressWarnings("unchecked")
public class StreamProcessorTest extends StreamPubSubTest {
  private SafeConsumerSink<Integer> sink;
  private StringToIntegerMapper transformer;

  @Test
  public void testThatProcessorPipesTransformation() {
    createPublisherWith(sourceOf123);

    final AccessSafely access = transformer.afterCompleting(6);

    final Subscriber<Integer> subscriber = createSubscriberWithoutSubscribing(sink, 3);

    final Processor<String,Integer> processor = world.actorFor(Processor.class, StreamProcessor.class, transformer, 3, PublisherConfiguration.defaultDropHead());

    processor.subscribe(subscriber);

    publisher.subscribe(processor);

    final int transformCount = access.readFrom("transformCount");

    Assert.assertEquals(3, transformCount);

    final List<Integer> values = access.readFrom("values");
    final List<Integer> expected = integerListOf1To(transformCount);

    Assert.assertEquals(expected, values);
  }

  @Test
  public void testThatProcessorPipesTransformationOfMany() {
    createPublisherWith(sourceRandomNumberOfElements);

    final AccessSafely accessTransformer = transformer.afterCompleting(200);

    @SuppressWarnings("unused")
    final AccessSafely accessSink = sink.afterCompleting(102);

    final Subscriber<Integer> subscriber = createSubscriberWithoutSubscribing(sink, 10);

    final Processor<String,Integer> processor = world.actorFor(Processor.class, StreamProcessor.class, transformer, 10, PublisherConfiguration.defaultDropHead());

    processor.subscribe(subscriber);

    publisher.subscribe(processor);

    final int sinkValue = sink.accessValueMustBe("value", 100);

    Assert.assertEquals(100, sinkValue);

    final int transformCount = accessTransformer.readFrom("transformCount");

    Assert.assertEquals(100, transformCount);

    final List<Integer> values = accessTransformer.readFrom("values");
    final List<Integer> expected = integerListOf1To(transformCount);

    Assert.assertEquals(expected, values);
  }

  @Override
  @Before
  public void setUp() {
    transformer = new StringToIntegerMapper();
    sink = new SafeConsumerSink<>();

    super.setUp();
  }
}
