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
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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

  @Test
  public void testThatMapperFlatMapsCollections() {
    final List<String> list1 = Arrays.asList("1", "2", "3");
    final List<String> list2 = Arrays.asList("4", "5", "6");
    final List<String> list3 = Arrays.asList("7", "8", "9");

    final List<List<String>> lists = Arrays.asList(list1, list2, list3);

    final List<Integer> results = new ArrayList<>();

    final Function<List<List<String>>, List<Integer>> mapper =
            (los) -> los.stream()
              .flatMap(list -> list.stream().map(s -> Integer.parseInt(s)))
              .collect(Collectors.toList());

    final Operator<List<List<String>>,List<Integer>> flatMapper = Operator.mapWith(mapper);

    flatMapper.performInto(lists, (numbers) -> results.addAll(numbers));

    Assert.assertEquals(9, results.size());
    Assert.assertEquals(1, (int) results.get(0));
    Assert.assertEquals(2, (int) results.get(1));
    Assert.assertEquals(3, (int) results.get(2));
  }


  @Test
  public void testThatMapperFlatMapsOptionals() {
    final List<Optional<String>> list1 = Arrays.asList(Optional.of("1"), Optional.of("2"), Optional.of("3"), Optional.empty());
    final List<Optional<String>> list2 = Arrays.asList(Optional.of("4"), Optional.empty(), Optional.of("5"), Optional.of("6"));
    final List<Optional<String>> list3 = Arrays.asList(Optional.of("7"), Optional.of("8"), Optional.empty(), Optional.of("9"));

    final List<List<Optional<String>>> lists = Arrays.asList(list1, list2, list3);

    final List<Optional<Integer>> results = new ArrayList<>();

    final Function<List<List<Optional<String>>>, List<Optional<Integer>>> mapper =
            (los) -> los.stream()
              .flatMap(list -> list.stream().filter(s -> s.isPresent()).map(s -> Optional.of(Integer.parseInt(s.get()))))
              .collect(Collectors.toList());

    final Operator<List<List<Optional<String>>>,List<Optional<Integer>>> flatMapper = Operator.mapWith(mapper);

    flatMapper.performInto(lists, (numbers) -> results.addAll(numbers));

    Assert.assertEquals(9, results.size());
    Assert.assertEquals(1, (int) results.get(0).get());
    Assert.assertEquals(2, (int) results.get(1).get());
    Assert.assertEquals(3, (int) results.get(2).get());
  }

}
