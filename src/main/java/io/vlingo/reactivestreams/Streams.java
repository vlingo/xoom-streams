// Copyright © 2012-2019 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams;

public class Streams {
  /**
   * The default total number of elements to buffer
   * internally before applying the {@code OverflowPolicy}.
   */
  static final int DefaultBufferSize = 256;

  /**
   * The default maximum number of elements to deliver
   * to the {@code Sink} before internally buffering.
   * The request maximum will be honored if the throttle
   * is higher than the remaining number requested. The
   * default of {@code -1} indicates to deliver up to the
   * remaining number of elements, up to the request maximum.
   */
  static final int DefaultMaxThrottle = -1;

  /**
   * Declares what the {@code Publisher} should do in the case
   * of the internal buffer reaching overflow of elements.
   */
  static enum OverflowPolicy {
    /**
     * Drops the head (first) element to make room for appending current as the tail.
     */
    DropHead,

    /**
     * Drops the tail (last) element to make room for appending current as the tail.
     */
    DropTail,

    /**
     * Drops the current element in favor of delivering the total number
     * of previously buffered elements.
     */
    DropCurrent
  }
}
