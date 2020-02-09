// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams.operator;

import io.vlingo.reactivestreams.Operator;
import io.vlingo.reactivestreams.source.LongRangeSource;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FlatMapperTest {
    @Test
    public void testThatPropagatesRecordsFromTheProvidedSink() {
        final Operator<Long, Long> flatMap = Operator.flatMapWith((record) -> new LongRangeSource(record, record + 2));
        final List<Long> providedLongs = new CopyOnWriteArrayList<>();

        flatMap.performInto(1L, providedLongs::add);
        flatMap.performInto(2L, providedLongs::add);

        Assert.assertEquals(Arrays.asList(1L, 2L, 2L, 3L), providedLongs);
    }
}
