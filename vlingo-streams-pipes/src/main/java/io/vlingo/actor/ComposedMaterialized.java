// Copyright © 2012-2019 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.pipes.actor;

import io.vlingo.actors.Actor;
import io.vlingo.actors.CompletesEventually;
import io.vlingo.common.Completes;

import java.util.List;

public class ComposedMaterialized extends Actor implements Materialized {
    private final List<Materialized> materializedList;

    public ComposedMaterialized(List<Materialized> materializedList) {
        this.materializedList = materializedList;
    }

    @Override
    public Completes<MaterializedSource> asSource() {
        CompletesEventually completes = completesEventually();
        materializedList.get(0).asSource().andFinallyConsume(completes::with);

        return completes();
    }

    @Override
    public void stop() {
        materializedList.forEach(Materialized::stop);
        super.stop();
    }
}
