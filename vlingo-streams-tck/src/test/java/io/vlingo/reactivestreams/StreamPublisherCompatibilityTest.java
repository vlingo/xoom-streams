// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams;

import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;

import io.vlingo.actors.Definition;
import io.vlingo.actors.World;

public class StreamPublisherCompatibilityTest extends PublisherVerification<Long> {

  public StreamPublisherCompatibilityTest() {
    super(new TestEnvironment());
  }

  @Override
  @SuppressWarnings("unchecked")
  public Publisher<Long> createPublisher(long l) {
    final World world = World.startWithDefaults("streams");

    final Source<Long> source = Source.with(LongStream.range(0, l).boxed().collect(Collectors.toList()));

    final Definition definition = Definition.has(StreamPublisher.class,
            Definition.parameters(source, PublisherConfiguration.defaultDropHead()));

    final Publisher<Long> publisher = world.actorFor(Publisher.class, definition);

    return publisher;
  }

  @Override
  public Publisher<Long> createFailedPublisher() {
    return s -> s.onError(new RuntimeException("Can't subscribe subscriber: " + s + ", because of reasons."));
  }

  @Override
  public long maxElementsFromPublisher() {
    return 1000;
  }
}
