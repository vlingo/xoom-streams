// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.reactivestreams.sink;

import java.util.function.Consumer;

public class TerminalOperationConsumerSink<T,O> extends ConsumerSink<T> {
  private final Consumer<O> terminalOperation;
  private final O terminalValue;

  public TerminalOperationConsumerSink(final Consumer<T> consumer, final O terminalValue, final Consumer<O> terminalOperation) {
    super(consumer);

    this.terminalOperation = terminalOperation;
    this.terminalValue = terminalValue;
  }

  @Override
  public void terminate() {
    super.terminate();

    terminalOperation.accept(terminalValue);
  }
}
