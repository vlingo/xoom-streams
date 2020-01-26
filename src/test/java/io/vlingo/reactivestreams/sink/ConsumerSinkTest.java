// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams.sink;

import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Test;

import io.vlingo.reactivestreams.Sink;

public class ConsumerSinkTest {
  private StringBuilder builder = new StringBuilder();

  @Test
  public void testThatSinkIsConsumed() throws Exception {
    final Consumer<String> consumer = (value) -> builder.append(value);

    final Sink<String> sink = Sink.consumeWith(consumer);

    sink.whenValue("A");
    sink.whenValue("B");
    sink.whenValue("C");

    Assert.assertEquals("ABC", builder.toString());
  }

  @Test
  public void testThatTerminatedSinkIsNotConsumed() throws Exception {
    final Consumer<String> consumer = (value) -> builder.append(value);

    final Sink<String> sink = Sink.consumeWith(consumer);

    sink.whenValue("A");
    sink.whenValue("B");
    sink.whenValue("C");

    sink.terminate();

    sink.whenValue("D");

    Assert.assertEquals("ABC", builder.toString());
  }
}
