package io.vlingo.pipes.actor;

import io.vlingo.common.Completes;
import io.vlingo.pipes.Record;

import java.util.Optional;

public interface MaterializedSource extends Materialized {
    Completes<Optional<Record[]>> nextIfAny();
}
