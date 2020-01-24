// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.tck.IdentityProcessorVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import io.vlingo.actors.World;
import io.vlingo.common.Completes;

public class StreamProcessorCompatibilityTest extends IdentityProcessorVerification<Integer> {

  public static final long DEFAULT_TIMEOUT_MILLIS = 300L;
  public static final long PUBLISHER_REFERENCE_CLEANUP_TIMEOUT_MILLIS = 1000L;

  private World world;

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
  @SuppressWarnings("unchecked")
  public Processor<Integer, Integer> createIdentityProcessor(int bufferSize) {
    Transformer<Integer, Integer> transformer = Completes::withSuccess;
    return world.actorFor(Processor.class, StreamProcessor.class, transformer, bufferSize,
            PublisherConfiguration.defaultDropHead());
  }

  @Override
  public Publisher<Integer> createFailedPublisher() {
    return new Publisher<Integer>() {
      @Override
      public void subscribe(Subscriber<? super Integer> s) {
        // Baffling. This proves nothing about the actual publisher, and shouldn't,
        // because designing the publisher with some sort of built-in failure
        // condition would be even more ridiculous than this. And BTW, the
        // subscriber will always be registered before onError() because there
        // is no way to signal a subscriber unless it is registered.
        s.onSubscribe(new SubscriptionController<>(s, null, null));
        s.onError(new RuntimeException("Can't subscribe subscriber: " + s + ", because of reasons."));
      }
    };
  }

  @Override
  public long maxElementsFromPublisher() {
    return super.maxElementsFromPublisher();
  }

  @Override
  public long boundedDepthOfOnNextAndRequestRecursion() {
    return super.boundedDepthOfOnNextAndRequestRecursion();
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
