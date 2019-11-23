package io.vlingo.pipes;

import io.vlingo.actors.Stage;
import io.vlingo.pipes.actor.Materialized;
import io.vlingo.pipes.actor.MaterializedSource;

public interface Materializable {
    Materialized materialize(Stage stage, MaterializedSource source);
}
