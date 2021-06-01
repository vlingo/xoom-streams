package io.vlingo.xoom.reactivestreams.operator;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.Assert;
import org.junit.Test;

import io.vlingo.xoom.reactivestreams.Operator;
import io.vlingo.xoom.reactivestreams.source.LongRangeSource;

public class FlatMapperTest {

  @Test
  public void testThatPropagatesRecordsFromTheProvidedSource() {
    final Operator<Long, Long> flatMap = Operator.flatMapWith((record) -> new LongRangeSource(record, record + 2));
    final List<Long> providedLongs = new CopyOnWriteArrayList<>();

    flatMap.performInto(1L, providedLongs::add);
    flatMap.performInto(3L, providedLongs::add);

    Assert.assertEquals(Arrays.asList(1L, 2L, 3L, 4L), providedLongs);
  }
}
