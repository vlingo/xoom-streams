// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.reactivestreams;

import java.util.List;

import io.vlingo.xoom.reactivestreams.sink.test.SafeConsumerSink;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.xoom.actors.testkit.AccessSafely;

public class StreamSubscriberTest extends StreamPubSubTest {
  private SafeConsumerSink<String> sink;

  @Test
  public void testThatSubscriberSubscribes() {
    createPublisherWith(sourceOfABC);

    createSubscriberWith(sink, 2);

    final AccessSafely access = sink.afterCompleting(1);

    publisher.subscribe(subscriber);

    final int subscriberCount = access.readFrom("ready");

    Assert.assertEquals(1, subscriberCount);
  }

  @Test
  public void testThatSubscriberFeedsSink() {
    createPublisherWith(sourceOfABC);

    createSubscriberWith(sink, 2);

    final AccessSafely access = sink.afterCompleting(5);

    publisher.subscribe(subscriber);

    final int subscriberCount = access.readFrom("ready");

    Assert.assertEquals(1, subscriberCount);

    final int valueCount = sink.accessValueMustBe("value", 3);

    Assert.assertEquals(3, valueCount);

    final int terminateCount = sink.accessValueMustBe("terminate", 1);

    Assert.assertEquals(1, terminateCount);

    final List<String> values = access.readFrom("values");

    Assert.assertEquals(3, values.size());

    Assert.assertEquals("A", values.get(0));
    Assert.assertEquals("B", values.get(1));
    Assert.assertEquals("C", values.get(2));
  }

  @Test
  public void testThatSubscriberReceivesTotalRandomNumberOfElements() {
    createPublisherWith(sourceRandomNumberOfElements);

    createSubscriberWith(sink, 5);

    final AccessSafely access = sink.afterCompleting(102);

    publisher.subscribe(subscriber);

    final int subscriberCount = access.readFrom("ready");

    Assert.assertEquals(1, subscriberCount);

    final int valueCount = sink.accessValueMustBe("value", 100);

    Assert.assertEquals(100, valueCount);

    final int terminateCount = sink.accessValueMustBe("terminate", 1);

    Assert.assertEquals(1, terminateCount);

    final List<String> values = access.readFrom("values");

    final List<String> expected = stringListOf1To(100);

    Assert.assertEquals(expected, values);
  }

  @Test
  public void testThatSubscriberReceivesUpToCancel() {
    createPublisherWith(sourceRandomNumberOfElements);

    final TestSubscriber<String> subscriber = new TestSubscriber<>(sink, 100, 50);

    final AccessSafely access = sink.afterCompleting(50);

    publisher.subscribe(subscriber);

    final int subscriberCount = access.readFrom("ready");

    Assert.assertEquals(1, subscriberCount);

    final int valueCount = sink.accessValueMustBe("value", 50);

    Assert.assertTrue(50 <= valueCount);

    final int terminateCount = sink.accessValueMustBe("terminate", 1);

    Assert.assertEquals(1, terminateCount);

    final List<String> values = access.readFrom("values");
    final List<String> expected = stringListOf1To(valueCount);

    Assert.assertEquals(expected, values);
  }

  @Override
  @Before
  public void setUp() {
    super.setUp();

    sink = new SafeConsumerSink<>();
  }
}
