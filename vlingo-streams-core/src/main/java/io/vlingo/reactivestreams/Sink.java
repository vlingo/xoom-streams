// Copyright © 2012-2019 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams;

import java.io.PrintStream;
import java.util.function.Consumer;

import io.vlingo.reactivestreams.sink.ConsumerSink;
import io.vlingo.reactivestreams.sink.PrintSink;

/**
 * The downstream receiver of values from a {@code Source<?>} with possible transformation to {@code T}.
 * @param <T> the type of the streamed values
 */
public interface Sink<T> {
  /**
   * Answer a new {@code Sink<T>} that relays to the {@code consumer}.
   * @param consumer the {@code Consumer<T>} that will consume values
   * @param <T> the type of values to be consumed
   * @return {@code Sink<T>}
   */
  public static <T> Sink<T> consumeWith(final Consumer<T> consumer) {
    return new ConsumerSink<>(consumer);
  }

  /**
   * Answer a new {@code Sink<T>} that prints to the standard output
   * stream where each line starts with {@code prefix}.
   * @param prefix the String that begins each line printed
   * @param <T> the type of values to be printed
   * @return {@code Sink<T>}
   */
  public static <T> Sink<T> printToStdout(final String prefix) {
    return printTo(System.out, prefix);
  }

  /**
   * Answer a new {@code Sink<T>} that prints to the standard error
   * stream where each line starts with {@code prefix}.
   * @param prefix the String that begins each line printed
   * @param <T> the type of values to be printed
   * @return {@code Sink<T>}
   */
  public static <T> Sink<T> printToStderr(final String prefix) {
    return printTo(System.err, prefix);
  }

  /**
   * Answer a new {@code Sink<T>} that prints to the given {@code printStream}
   * where each line starts with {@code prefix}.
   * @param printStream the {@code PrintStream} where output values are printed
   * @param prefix the String that begins each line printed
   * @param <T> the type of values to be printed
   * @return {@code Sink<T>}
   */
  public static <T> Sink<T> printTo(final PrintStream printStream, final String prefix) {
    return new PrintSink<>(printStream, prefix);
  }

  /**
   * Indicates that the receiver should become ready to receive values.
   */
  void ready();

  /**
   * Indicates that the receiver has been terminated.
   */
  void terminate();

  /**
   * Receives the new {@code value} from the stream.
   * @param value the next T value from the stream
   */
  void whenValue(final T value);
}
