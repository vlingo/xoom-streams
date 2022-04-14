// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.reactivestreams.sink;

import io.vlingo.xoom.reactivestreams.Sink;

/**
 * A {@code Sink<T>} that does nothing.
 *
 * @param <T> the type of the value to be provided
 */
public class NoOpSink<T> implements Sink<T> {
  /**
   * Constructs my state.
   */
  public NoOpSink() { }

  @Override
  public void ready() {
    // ignored
  }

  @Override
  public void terminate() {
    // ignored
  }

  @Override
  public void whenValue(final T value) {
    // ignored
  }

  @Override
  public String toString() {
    return "NoOpConsumerSink[nothing=nothing]";
  }
}
