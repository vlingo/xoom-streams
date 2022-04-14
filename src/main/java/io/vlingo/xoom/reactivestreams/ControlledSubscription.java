// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.reactivestreams;

public interface ControlledSubscription<T> {
  public void cancel(final SubscriptionController<T> subscription);
  public void request(final SubscriptionController<T> subscription, final long maximum);
}
