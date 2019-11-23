package io.vlingo.pipes.actor;

import io.vlingo.common.Completes;

import java.util.Optional;

public interface MaterializedSource extends Materialized {
    Completes<Optional<Object[]>> nextIfAny();
}
