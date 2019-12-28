// Copyright © 2012-2019 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams.sink;

import java.io.PrintStream;

import io.vlingo.reactivestreams.Sink;

/**
 * A {@code Sink<T>} that prints to the given {@code PrintStream}, and may prefix
 * the output with a {@code String} value.
 *
 * @param <T> the type of the value to be printed
 */
public class PrintSink<T> implements Sink<T> {
  private final PrintStream printStream;
  private final String prefix;
  private boolean terminated;

  public PrintSink(final PrintStream printStream, final String prefix) {
    this.printStream = printStream;
    this.prefix = prefix;
  }

  @Override
  public void terminate() {
    terminated = true;
  }

  @Override
  public void whenValue(final T value) {
    if (!terminated) {
      printStream.println(prefix + value.toString());
    }
  }
}
