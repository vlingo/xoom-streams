// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.vlingo.actors.testkit.AccessSafely;

public class StreamPublisherTest extends StreamPubSubTest {

  @Test
  public void testThatSubscriberSubscribes() {
    createPublisherWith(sourceOfABC);

    final TestSubscriber<String> subscriber = new TestSubscriber<>(3);

    final AccessSafely access = subscriber.afterCompleting(1);

    publisher.subscribe(subscriber);

    final int subscriberCount = access.readFrom("onSubscribe");

    Assert.assertEquals(1, subscriberCount);
  }

  @Test
  public void testThatSubscriberReceives() {
    createPublisherWith(sourceOfABC);

    final TestSubscriber<String> subscriber = new TestSubscriber<>(3);

    final AccessSafely access = subscriber.afterCompleting(8);

    publisher.subscribe(subscriber);

    final int subscriberCount = access.readFrom("onSubscribe");

    Assert.assertEquals(1, subscriberCount);

    final int completedCount = access.readFrom("onComplete");

    Assert.assertEquals(1, completedCount);

    final int valueCount = subscriber.accessValueMustBe("onNext", 3);

    Assert.assertEquals(3, valueCount);

    final List<String> values = access.readFrom("values");
    final List<String> expected = Arrays.asList("A", "B", "C");

    Assert.assertEquals(expected, values);
  }

  @Test
  public void testThatSubscriberReceivesTotalRandomNumberOfElements() {
    createPublisherWith(sourceRandomNumberOfElements);

    final TestSubscriber<String> subscriber = new TestSubscriber<>(100);

    final AccessSafely access = subscriber.afterCompleting(102);

    publisher.subscribe(subscriber);

    final int subscriberCount = access.readFrom("onSubscribe");

    Assert.assertEquals(1, subscriberCount);

    final int completedCount = subscriber.accessValueMustBe("onComplete", 1); // access.readFrom("onComplete");

    Assert.assertEquals(1, completedCount);

    final int valueCount = subscriber.accessValueMustBe("onNext", 100);

    Assert.assertEquals(100, valueCount);

    final List<String> values = access.readFrom("values");
    final List<String> expected = listOf1To(100);

    Assert.assertEquals(expected, values);
  }

  @Test
  public void testThatSubscriberReceivesUpToCancel() {
    createPublisherWith(sourceRandomNumberOfElements);

    final TestSubscriber<String> subscriber = new TestSubscriber<>(100, 50);

    final AccessSafely access = subscriber.afterCompleting(50);

    publisher.subscribe(subscriber);

    final int subscriberCount = access.readFrom("onSubscribe");

    Assert.assertEquals(1, subscriberCount);

    final int valueCount = subscriber.accessValueMustBe("onNext", 50);

    // System.out.println("valueCount=" + valueCount);

    Assert.assertTrue(50 <= valueCount);

    final List<String> values = access.readFrom("values");
    final List<String> expected = listOf1To(valueCount);

    Assert.assertEquals(expected, values);
  }

  @Test
  public void testThatSubscriberReceivesFromSlowSource() {
    sourceRandomNumberOfElements = new RandomNumberOfElementsSource(100, true);

    createPublisherWith(sourceRandomNumberOfElements);

    final TestSubscriber<String> subscriber = new TestSubscriber<>(100);

    final AccessSafely access = subscriber.afterCompleting(102);

    publisher.subscribe(subscriber);

    final int subscriberCount = access.readFrom("onSubscribe");

    Assert.assertEquals(1, subscriberCount);

    final int completedCount = subscriber.accessValueMustBe("onComplete", 1); // access.readFrom("onComplete");

    Assert.assertEquals(1, completedCount);

    final int valueCount = subscriber.accessValueMustBe("onNext", 100);

    Assert.assertEquals(100, valueCount);

    final List<String> values = access.readFrom("values");
    final List<String> expected = listOf1To(100);

    Assert.assertEquals(expected, values);
  }
}
