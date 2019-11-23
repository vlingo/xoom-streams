package io.vlingo.pipes.actor;

import io.vlingo.actors.Actor;
import io.vlingo.common.Cancellable;
import io.vlingo.common.Completes;
import io.vlingo.common.Scheduled;
import io.vlingo.pipes.Operator;

import java.util.Arrays;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Stream;

public class MaterializedOperatorActor extends Actor implements MaterializedSource, Scheduled<Void> {
    private final MaterializedSource previousSource;
    private final Operator<Object, Object> operator;
    private final int pollingInterval;
    private final Cancellable cancellable;
    private final Queue<Object> queue;
    private MaterializedSource selfAsMaterializedSource;

    public MaterializedOperatorActor(MaterializedSource previousSource, Operator<Object, Object> operator, int pollingInterval) {
        this.selfAsMaterializedSource = selfAs(MaterializedSource.class);
        this.previousSource = previousSource;
        this.operator = operator;
        this.pollingInterval = pollingInterval;
        this.queue = new PriorityQueue<>();
        this.cancellable = scheduler().schedule(selfAs(Scheduled.class), null, 0, pollingInterval);
    }

    @Override
    public void intervalSignal(Scheduled<Void> scheduled, Void aVoid) {
        previousSource.nextIfAny()
                .andThenConsume((e) -> operator.poll().thenAccept(record -> queue.addAll(Arrays.asList(record))))
                .andFinallyConsume(e -> e.ifPresent(this::whenValueForEach));
    }

    @Override
    public Completes<Optional<Object[]>> nextIfAny() {
        if (queue.isEmpty()) {
            return completes().with(Optional.empty());
        }

        int size = queue.size();
        Object[] res = new Object[size];
        for (var i = 0; i < size; i++) {
            res[i] = queue.poll();
        }

        return completes().with(Optional.ofNullable(res));
    }

    @Override
    public void stop() {
        this.cancellable.cancel();
        super.stop();
    }

    @Override
    public Completes<MaterializedSource> asSource() {
        return completes().with(selfAsMaterializedSource);
    }

    private void whenValueForEach(Object[] e) {
        Stream.of(e).forEach(operator::whenValue);
    }
}
