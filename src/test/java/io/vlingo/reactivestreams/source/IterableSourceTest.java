// Copyright © 2012-2019 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams.source;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import io.vlingo.reactivestreams.Source;

public class IterableSourceTest extends SourceTest {
  @Test
  public void testThatEmptyHasNoElements() {
    final Source<String> source = Source.empty();

    final String result = stringFromSource(source);

    Assert.assertTrue(result.isEmpty());
  }

  @Test
  public void testThatSourceProvidesElements() {
    final Source<String> source = Source.only("A", "B", "C");

    final String result = stringFromSource(source);

    Assert.assertEquals("ABC", result);
  }

  @Test
  public void testThatSourceCollectionProvidesElements() {
    final Source<String> source = Source.with(Arrays.asList("A", "B", "C"));

    final String result = stringFromSource(source);

    Assert.assertEquals("ABC", result);
  }
}
