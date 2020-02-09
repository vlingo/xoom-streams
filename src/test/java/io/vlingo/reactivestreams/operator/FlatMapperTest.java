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
    public void testThatPropagatesRecordsFromTheProvidedSink() throws InterruptedException {
        final Operator<Long, Long> flatMap = Operator.flatMapWith((record) -> new LongRangeSource(record, record + 2));
        final List<Long> providedLongs = new CopyOnWriteArrayList<>();

        flatMap.performInto(1L, providedLongs::add);
        flatMap.performInto(2L, providedLongs::add);

        Thread.sleep(1000);
        Assert.assertEquals(Arrays.asList(1L, 2L, 2L, 3L), providedLongs);
    }
}
