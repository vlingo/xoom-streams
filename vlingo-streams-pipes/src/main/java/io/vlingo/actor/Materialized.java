// Copyright © 2012-2019 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.pipes.actor;

import io.vlingo.actors.Stoppable;
import io.vlingo.common.Completes;

public interface Materialized extends Stoppable {
    Completes<MaterializedSource> asSource();
}
