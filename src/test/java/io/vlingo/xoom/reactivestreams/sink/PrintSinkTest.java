// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.reactivestreams.sink;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Test;

import io.vlingo.xoom.reactivestreams.Sink;

public class PrintSinkTest {
  private ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
  private PrintStream printStream = new PrintStream(byteStream);

  @Test
  public void testThatSinkPrintsToStream() throws Exception {
    final Sink<String> sink = new PrintSink<>(printStream, "");

    sink.whenValue("A");
    sink.whenValue("B");
    sink.whenValue("C");

    Assert.assertEquals("A\nB\nC\n", printString());
  }

  @Test
  public void testThatSinkPrintsWithPrefixToStream() throws Exception {
    final Sink<String> sink = Sink.printTo(printStream, "-");

    sink.whenValue("A");
    sink.whenValue("B");
    sink.whenValue("C");

    Assert.assertEquals("-A\n-B\n-C\n", printString());

  }

  @Test
  public void testThatTerminatedSinkDoesNotPrint() throws Exception {
    final Sink<String> sink = new PrintSink<>(printStream, "");

    sink.whenValue("A");
    sink.whenValue("B");
    sink.whenValue("C");

    sink.terminate();

    sink.whenValue("D");

    Assert.assertEquals("A\nB\nC\n", printString());
  }

  private String printString() throws Exception {
    return byteStream.toString("UTF8");
  }
}
