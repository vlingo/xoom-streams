package io.vlingo.pipes.actor;

import io.vlingo.actors.Stoppable;
import io.vlingo.common.Completes;

public interface Materialized extends Stoppable {
    Completes<MaterializedSource> asSource();
}
