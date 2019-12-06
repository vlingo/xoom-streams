// Copyright © 2012-2019 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

import io.vlingo.common.Tuple2;
import io.vlingo.http.Method;
import io.vlingo.http.Response;
import io.vlingo.pipes.Streams;
import io.vlingo.pipes.sinks.SubscriptionSink;
import io.vlingo.pipes.sources.SupplierSource;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static io.vlingo.http.resource.serialization.JsonSerialization.serialized;

public class MyApp {
    public static CompletableFuture<String> enrich(Tuple2<String, UUID> tuple) {
        return CompletableFuture.supplyAsync(
                () -> String.format("%s-%s", tuple._1, tuple._2),
                CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS)
        );
    }

    public static void main(String[] args) throws InterruptedException {
        Streams streams = Streams.app("my-app");

        streams.from(streams.http.requestSource(Method.POST, "/"))
                .map(e -> e.body.content())
                .map(String::toUpperCase)
                .zip(SupplierSource.fromSupplier(UUID::randomUUID))
                .flatMapFuture(MyApp::enrich)
                .map(e -> Response.of(Response.Status.Ok, serialized(e)))
                .to(streams.http.responseSink());

        streams.from(streams.http.requestSource(Method.GET, "/close"))
                .map(e -> "We are closing the streams application.")
                .zip(SupplierSource.fromSupplier(Instant::now))
                .map(e -> Response.of(Response.Status.Ok, serialized(e)))
                .through(streams.http.responseSink())
                .delay(1, TimeUnit.SECONDS)
                .to(SubscriptionSink.subscribingWith(request -> streams.close()));

        streams.start();
    }
}
