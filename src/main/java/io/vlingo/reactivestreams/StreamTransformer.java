// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.reactivestreams;

import io.vlingo.actors.Actor;
import io.vlingo.common.Completes;

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
