package io.vlingo.pipes;

import java.util.concurrent.CompletableFuture;

public interface Source<T> extends Materializable  {
    CompletableFuture<T[]> poll();
}
