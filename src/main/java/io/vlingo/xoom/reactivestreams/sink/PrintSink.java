// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.reactivestreams.sink;

import java.io.PrintStream;

import io.vlingo.xoom.reactivestreams.Sink;

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

  /**
   * Constructs my default state.
   * @param printStream the PrintStream through which to print my values
   * @param prefix the String used to begin each printed line
   */
  public PrintSink(final PrintStream printStream, final String prefix) {
    this.printStream = printStream;
    this.prefix = prefix;
    this.terminated = false;
  }

  @Override
  public void ready() {
    // ignored
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

  @Override
  public String toString() {
    return "PrintSink[terminated=" + terminated + "]";
  }
}
