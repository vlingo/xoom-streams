// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.tck.SubscriberBlackboxVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import io.vlingo.actors.World;
import io.vlingo.reactivestreams.sink.NoOpSink;

public class StreamSubscriberCompatibilityTest extends SubscriberBlackboxVerification<Integer> {
  private World world;

  public StreamSubscriberCompatibilityTest() {
    super(new TestEnvironment());
  }

  @Override
  public Integer createElement(int element) {
    return element;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Subscriber<Integer> createSubscriber() {
    final World world = World.startWithDefaults("streams");
    final Sink<Integer> sink = new NoOpSink<>();
    return world.actorFor(Subscriber.class, StreamSubscriber.class, sink, 10);
  }

  @BeforeMethod
  public void before() {
    world = World.startWithDefaults("streams");
  }

  @AfterMethod
  public void after() {
    world.terminate();
  }
}
