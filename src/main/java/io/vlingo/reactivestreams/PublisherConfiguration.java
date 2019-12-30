// Copyright © 2012-2019 Vaughn Vernon. All rights reserved.
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
  public final int bufferSize;
  public final int maxThrottle;
  public final OverflowPolicy overflowPolicy;
  public final int probeInterval;

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
