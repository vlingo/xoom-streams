// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.reactivestreams.sink;

import java.util.function.Consumer;

import io.vlingo.xoom.reactivestreams.Sink;

/**
 * A {@code Sink<T>} that provides values to a given {@code Consumer<T>}.
 *
 * @param <T> the type of the value to be provided to the {@code Consumer<T>}.
 */
public class ConsumerSink<T> implements Sink<T> {
  private final Consumer<T> consumer;
  private boolean terminated;

  /**
   * Constructs my default state.
   * @param consumer the {@code Consumer<T>} to accept my values
   */
  public ConsumerSink(final Consumer<T> consumer) {
    this.consumer = consumer;
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
      consumer.accept(value);
    }
  }

  @Override
  public String toString() {
    return "ConsumerSink[terminated=" + terminated + "]";
  }
}
