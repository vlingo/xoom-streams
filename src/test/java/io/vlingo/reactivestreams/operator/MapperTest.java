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

public class MapperTest {

  @Test
  public void testThatMapperMaps() {
    final Operator<String,Integer> mapper = Operator.mapWith((s) -> Integer.parseInt(s));

    final List<Integer> results = new ArrayList<>();

    Arrays.asList("123", "456", "789")
      .forEach(digits -> mapper.performInto(digits, (number) -> results.add(number)));

    Assert.assertEquals(3, results.size());
    Assert.assertEquals(123, (int) results.get(0));
    Assert.assertEquals(456, (int) results.get(1));
    Assert.assertEquals(789, (int) results.get(2));
  }
}
