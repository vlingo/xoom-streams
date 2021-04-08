// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.reactivestreams.source;

import org.junit.Assert;
import org.junit.Test;

import io.vlingo.xoom.reactivestreams.Source;

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

  @Test
  public void testThatZeroZeroRangeCompletes() {
    final Source<Long> range = Source.rangeOf(0, 0);

    range.next().andFinallyConsume(elements -> {
      System.out.println("E: " + elements.elementsAsString());
      if (!elements.terminated) {
        current = elements.values[0];
        Assert.assertEquals(expected, current);
      } else {
        terminated = true;
      }
    });

    Assert.assertTrue(terminated);
  }

  @Test(expected=Throwable.class)
  public void testThatOverflowThrows() {
    new LongRangeSource(0, Long.MAX_VALUE + 1);
  }

  @Test(expected = Throwable.class)
  public void testThatStartUnderflowThrows() {
    new LongRangeSource(-1, Long.MAX_VALUE);
  }
}
