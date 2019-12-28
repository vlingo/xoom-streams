// Copyright © 2012-2019 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams.sink;

import java.util.function.Consumer;

import io.vlingo.reactivestreams.Sink;

public class ConsumerSink<T> implements Sink<T> {
  private final Consumer<T> consumer;
  private boolean terminated;

  public ConsumerSink(final Consumer<T> consumer) {
    this.consumer = consumer;
    this.terminated = false;
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
}
