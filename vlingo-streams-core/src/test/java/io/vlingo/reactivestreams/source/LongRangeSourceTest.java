// Copyright © 2012-2019 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams.source;

import org.junit.Assert;
import org.junit.Test;

import io.vlingo.reactivestreams.Source;

public class LongRangeSourceTest {
  private long current = 0;
  private long expected = 0;
  private boolean terminated = false;

  @Test
  public void testThatRangeCompletes() {
    final Source<Long> range = Source.rangeOf(1, 11);

    while (!terminated) {
      ++expected;
      range.next().andFinallyConsume(elements -> {
        if (!elements.terminated) {
          current = elements.values[0];
          Assert.assertEquals(expected, current);
        }
        terminated = elements.terminated;
      });
    }

    Assert.assertEquals(10, current);
  }
}
