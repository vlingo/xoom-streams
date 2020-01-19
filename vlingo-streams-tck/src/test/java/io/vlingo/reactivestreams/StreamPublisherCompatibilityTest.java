// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams;

import static io.vlingo.reactivestreams.Source.orElseMaximum;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import io.vlingo.actors.Definition;
import io.vlingo.actors.World;
import io.vlingo.reactivestreams.Streams.OverflowPolicy;

public class StreamPublisherCompatibilityTest extends PublisherVerification<Long> {
  private World world;

  public StreamPublisherCompatibilityTest() {
    super(new TestEnvironment(400, 200, 100, true)); // new TestEnvironment(2000, 2000, 100, true)
  }

  @Override
  @SuppressWarnings("unchecked")
  public Publisher<Long> createPublisher(final long elements) {
    final Source<Long> source = Source.rangeOf(1, orElseMaximum(elements + 1));

    final PublisherConfiguration configuration =
            PublisherConfiguration.with(
                    PublisherConfiguration.FastestProbeInterval,
                    Streams.DefaultMaxThrottle,
                    Streams.DefaultBufferSize,
                    OverflowPolicy.DropHead);

    final Definition definition = Definition.has(StreamPublisher.class, Definition.parameters(source, configuration));

    final Publisher<Long> publisher = world.actorFor(Publisher.class, definition);

    return publisher;
  }

  @Override
  public Publisher<Long> createFailedPublisher() {
    return new Publisher<Long>() {
      @Override
      public void subscribe(Subscriber<? super Long> subscriber) {
        // Baffling. This proves nothing about the actual publisher, and shouldn't,
        // because designing the publisher with some sort of built-in failure
        // condition would be even more ridiculous than this. And BTW, the
        // subscriber will always be registered before onError() because there
        // is no way to signal a subscriber unless it is registered.
        subscriber.onSubscribe(new SubscriptionController<>(subscriber, null, null));
        subscriber.onError(new RuntimeException("Can't subscribe subscriber: " + subscriber + ", because of reasons."));
      }
    };
  }

  @BeforeMethod
  public void before() {
    world = World.startWithDefaults("streams");
  }

  @AfterMethod
  public void after() {
    world.terminate();
  }

//  @Test
//  @Override
//  public void required_createPublisher1MustProduceAStreamOfExactly1Element() throws Throwable {
//    super.required_createPublisher1MustProduceAStreamOfExactly1Element();
//  }
//
//  @Test
//  @Override
//  public void required_createPublisher3MustProduceAStreamOfExactly3Elements() throws Throwable {
//    super.required_createPublisher3MustProduceAStreamOfExactly3Elements();
//  }
//
//  @Test
//  @Override
//  public void required_spec101_subscriptionRequestMustResultInTheCorrectNumberOfProducedElements() throws Throwable {
//    super.required_spec101_subscriptionRequestMustResultInTheCorrectNumberOfProducedElements();
//  }
//
//  @Test
//  @Override
//  public void required_spec102_maySignalLessThanRequestedAndTerminateSubscription() throws Throwable {
//    super.required_spec102_maySignalLessThanRequestedAndTerminateSubscription();
//  }
//
//  @Test
//  @Override
//  public void stochastic_spec103_mustSignalOnMethodsSequentially() throws Throwable {
//    super.stochastic_spec103_mustSignalOnMethodsSequentially();
//  }
//
//  @Test
//  @Override
//  public void optional_spec104_mustSignalOnErrorWhenFails() throws Throwable {
//    super.optional_spec104_mustSignalOnErrorWhenFails();
//  }
//
//  @Test
//  @Override
//  public void required_spec105_mustSignalOnCompleteWhenFiniteStreamTerminates() throws Throwable {
//    super.required_spec105_mustSignalOnCompleteWhenFiniteStreamTerminates();
//  }
//
//  @Test
//  @Override
//  public void optional_spec105_emptyStreamMustTerminateBySignallingOnComplete() throws Throwable {
//    super.optional_spec105_emptyStreamMustTerminateBySignallingOnComplete();
//  }
//
//  @Test
//  @Override
//  public void required_spec107_mustNotEmitFurtherSignalsOnceOnCompleteHasBeenSignalled() throws Throwable {
//    super.required_spec105_mustSignalOnCompleteWhenFiniteStreamTerminates();
//  }
//
//  @Test
//  @Override
//  public void required_spec109_mustIssueOnSubscribeForNonNullSubscriber() throws Throwable {
//    super.required_spec109_mustIssueOnSubscribeForNonNullSubscriber();
//  }
//
//  @Test
//  @Override
//  public void required_spec302_mustAllowSynchronousRequestCallsFromOnNextAndOnSubscribe() throws Throwable {
//    super.required_spec302_mustAllowSynchronousRequestCallsFromOnNextAndOnSubscribe();
//  }
//
//  @Test
//  @Override
//  public void required_spec303_mustNotAllowUnboundedRecursion() throws Throwable {
//    super.required_spec303_mustNotAllowUnboundedRecursion();
//  }
//
//  @Test
//  @Override
//  public void required_spec306_afterSubscriptionIsCancelledRequestMustBeNops() throws Throwable {
//    super.required_spec306_afterSubscriptionIsCancelledRequestMustBeNops();
//  }
//
//  @Test
//  @Override
//  public void required_spec307_afterSubscriptionIsCancelledAdditionalCancelationsMustBeNops() throws Throwable {
//    super.required_spec307_afterSubscriptionIsCancelledAdditionalCancelationsMustBeNops();
//  }
//
//  @Test
//  @Override
//  public void required_spec309_requestNegativeNumberMustSignalIllegalArgumentException() throws Throwable {
//    super.required_spec309_requestNegativeNumberMustSignalIllegalArgumentException();
//  }
//
//  @Test
//  @Override
//  public void required_spec309_requestZeroMustSignalIllegalArgumentException() throws Throwable {
//    super.required_spec309_requestZeroMustSignalIllegalArgumentException();
//  }
//
//  @Test
//  @Override
//  public void required_spec312_cancelMustMakeThePublisherToEventuallyStopSignaling() throws Throwable {
//    super.required_spec312_cancelMustMakeThePublisherToEventuallyStopSignaling();
//  }
//
//  @Test
//  @Override
//  public void required_spec313_cancelMustMakeThePublisherEventuallyDropAllReferencesToTheSubscriber() throws Throwable {
//    super.required_spec313_cancelMustMakeThePublisherEventuallyDropAllReferencesToTheSubscriber();
//  }
//
//  @Test
//  @Override
//  public void required_spec317_mustNotSignalOnErrorWhenPendingAboveLongMaxValue() throws Throwable {
//    super.required_spec317_mustNotSignalOnErrorWhenPendingAboveLongMaxValue();
//  }
//
//  @Test
//  @Override
//  public void required_spec317_mustSupportACumulativePendingElementCountUpToLongMaxValue() throws Throwable {
//    super.required_spec317_mustSupportACumulativePendingElementCountUpToLongMaxValue();
//  }
//
//  @Test
//  @Override
//  public void required_spec317_mustSupportAPendingElementCountUpToLongMaxValue() throws Throwable {
//    super.required_spec317_mustSupportAPendingElementCountUpToLongMaxValue();
//  }
}
