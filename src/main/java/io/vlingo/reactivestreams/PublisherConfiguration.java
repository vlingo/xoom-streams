// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams;

import io.vlingo.reactivestreams.Streams.OverflowPolicy;

/**
 * The standard configuration for any {@code StreamPublisher}.
 */
public class PublisherConfiguration {
  public static int DefaultProbeInterval = 5;
  public static int FastProbeInterval = 2;
  public static int FastestProbeInterval = 1;

  public final int bufferSize;
  public final int maxThrottle;
  public final OverflowPolicy overflowPolicy;
  public final int probeInterval;

  public static PublisherConfiguration defaultDropCurrent() {
    return new PublisherConfiguration(DefaultProbeInterval, Streams.DefaultMaxThrottle, Streams.DefaultBufferSize, OverflowPolicy.DropCurrent);
  }

  public static PublisherConfiguration defaultDropHead() {
    return new PublisherConfiguration(DefaultProbeInterval, Streams.DefaultMaxThrottle, Streams.DefaultBufferSize, OverflowPolicy.DropHead);
  }

  public static PublisherConfiguration defaultDropTail() {
    return new PublisherConfiguration(DefaultProbeInterval, Streams.DefaultMaxThrottle, Streams.DefaultBufferSize, OverflowPolicy.DropTail);
  }

  public static PublisherConfiguration with(final int probeInterval, final int maxThrottle, final int bufferSize, final OverflowPolicy overflowPolicy) {
    return new PublisherConfiguration(probeInterval, maxThrottle, bufferSize, overflowPolicy);
  }

  public PublisherConfiguration(final int probeInterval, final int maxThrottle, final int bufferSize, final OverflowPolicy overflowPolicy) {
    assert(probeInterval > 0);
    this.probeInterval = probeInterval;
    assert(maxThrottle == Streams.DefaultMaxThrottle || maxThrottle > 0);
    this.maxThrottle = maxThrottle;
    assert(bufferSize > 0);
    this.bufferSize = bufferSize;
    assert(overflowPolicy != null);
    this.overflowPolicy = overflowPolicy;
  }

  public PublisherConfiguration(final int probeInterval, final OverflowPolicy overflowPolicy) {
    this(probeInterval, Streams.DefaultMaxThrottle, Streams.DefaultBufferSize, overflowPolicy);
  }
}
