// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import io.vlingo.actors.testkit.AccessSafely;
import io.vlingo.common.Completes;

public class StringToIntegerTransformer implements Transformer<String,Integer> {
  private AccessSafely access = AccessSafely.afterCompleting(0);

  private final AtomicInteger transformCount = new AtomicInteger(0);
  private final List<Integer> values = new CopyOnWriteArrayList<>();

  @Override
  public Completes<Integer> transform(final String value) {
    final int amount = Integer.valueOf(value);
    access.writeUsing("values", amount);
    access.writeUsing("transformCount", 1);
    return Completes.withSuccess(amount);
  }

  public AccessSafely afterCompleting(final int times) {
    access = AccessSafely.afterCompleting(times);

    access.writingWith("transformCount", (Integer value) -> transformCount.addAndGet(value));

    access.writingWith("values", (Integer value) -> values.add(value));

    access.readingWith("transformCount", () -> transformCount.get());

    access.readingWith("values", () -> values);

    return access;
  }

  public int accessValueMustBe(final String name, final int expected) {
    int current = 0;
    for (int tries = 0; tries < 10; ++tries) {
      final int value = access.readFrom(name);
      if (value >= expected) {
        return value;
      }
      if (current != value) {
        current = value;
        // System.out.println("VALUE: " + value);
      }
      try { Thread.sleep(100); } catch (Exception e) { }
    }
    return expected == 0 ? -1 : 0;
  }

  @Override
  public String toString() {
    return "StringToIntegerTransformer";
  }
}
