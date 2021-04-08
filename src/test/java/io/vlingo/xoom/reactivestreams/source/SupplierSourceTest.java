// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.reactivestreams.source;

import java.util.function.Supplier;

import org.junit.Assert;
import org.junit.Test;

import io.vlingo.xoom.reactivestreams.Source;

public class SupplierSourceTest extends SourceTest {
  private int index;

  @Test
  public void testThatSourceSupplierProvidesElements() {
    index = 0;

    final Supplier<String> supplier = () -> {
      if (index >= 3) return null;
      final char next = (char) ('A' + index);
      final String value = String.valueOf(next);
      ++index;
      return value;
    };

    final Source<String> source = Source.with(supplier);

    final String result = stringFromSource(source);

    Assert.assertEquals("ABC", result);
  }
}
