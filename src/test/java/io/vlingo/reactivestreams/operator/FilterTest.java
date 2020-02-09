// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams.operator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.vlingo.reactivestreams.Operator;

public class FilterTest {

  @Test
  public void testThatFilterFilters() {
    final Operator<String,String> filter = Operator.filterWith((s) -> s.contains("1"));

    final List<String> results = new ArrayList<>();

    Arrays.asList("ABC", "321", "123", "456", "DEF", "214")
      .forEach(possible -> filter.performInto(possible, (match) -> results.add(match)));

    Assert.assertEquals(3, results.size());
    Assert.assertEquals("321", results.get(0));
    Assert.assertEquals("123", results.get(1));
    Assert.assertEquals("214", results.get(2));
  }
}
