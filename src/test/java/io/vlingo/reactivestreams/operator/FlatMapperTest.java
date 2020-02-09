// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams.operator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.World;
import io.vlingo.reactivestreams.Streams;

public class FlatMapperTest {
  private World world;

  @Test
  public void testThatFlatMapperMaps() {
//    final Operator<List<List<String>>, List<Integer>> flatMapper =
//            Operator.flatMapWith((lists) ->
//                      lists.stream().map((list) -> list.stream().map(str -> Integer.parseInt(str))).collect(Collectors.toList())
//                      );
//
    final List<String> list1 = Arrays.asList("1", "2", "3");
    final List<String> list2 = Arrays.asList("4", "5", "6");
    final List<String> list3 = Arrays.asList("7", "8", "9");

    final List<List<String>> lists = Arrays.asList(list1, list2, list3);

//    final List<Integer> results = new ArrayList<>();
//
//    flatMapper.performInto(lists, (number) -> results.addAll(number));
//
//    Assert.assertEquals(9, results.size());
//    Assert.assertEquals(1, (int) results.get(0));
//    Assert.assertEquals(2, (int) results.get(1));
//    Assert.assertEquals(3, (int) results.get(2));

    List<Integer> flat2 =
            lists
              .stream()
              .flatMap(list -> list.stream().map(s -> Integer.parseInt(s)))
              .collect(Collectors.toList());

    Assert.assertEquals(9, flat2.size());
    Assert.assertEquals(1, (int) flat2.get(0));
    Assert.assertEquals(2, (int) flat2.get(1));
    Assert.assertEquals(3, (int) flat2.get(2));
  }

  @Before
  public void setUp() {
    world = World.startWithDefaults("flatMap");
    Streams.logger(world.defaultLogger());
  }
}
