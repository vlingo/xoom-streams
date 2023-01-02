// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.reactivestreams.source;

import io.vlingo.xoom.reactivestreams.Elements;
import io.vlingo.xoom.reactivestreams.Source;

public abstract class SourceTest {
  private final StringBuilder builder = new StringBuilder();

  protected String stringFromSource(final Source<String> source) {
    String current = "";

    while (current != null)  {
      final Elements<String> elements = source.next().andFinally().await();

      current = elements.elementAt(0);

      if (current != null) {
        builder.append(current);
      }
    }

    final String result = builder.toString();

    return result;
  }
}
