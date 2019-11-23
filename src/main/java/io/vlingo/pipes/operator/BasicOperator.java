package io.vlingo.pipes.operator;

import io.vlingo.actors.Stage;
import io.vlingo.pipes.Operator;
import io.vlingo.pipes.Stream;
import io.vlingo.pipes.actor.Materialized;
import io.vlingo.pipes.actor.MaterializedOperatorActor;
import io.vlingo.pipes.actor.MaterializedSource;

public abstract class BasicOperator<A, B> implements Operator<A, B> {
    @Override
    public Materialized materialize(Stage stage, MaterializedSource source) {
        return stage.actorFor(MaterializedSource.class, MaterializedOperatorActor.class, source, this, Stream.DEFAULT_POLL_INTERVAL);
    }
}
