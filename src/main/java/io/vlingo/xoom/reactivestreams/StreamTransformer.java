// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.reactivestreams;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.common.Completes;

public class StreamTransformer<T,R> extends Actor implements Transformer<T,R> {
  private final Transformer<T,R> transformer;

  public StreamTransformer(final Transformer<T,R> transformer) {
    this.transformer = transformer;
  }

  @Override
  public Completes<R> transform(final T value) {
    return transformer.transform(value);
  }
}
