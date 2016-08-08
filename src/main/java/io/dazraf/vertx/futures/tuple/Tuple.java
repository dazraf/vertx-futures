package io.dazraf.vertx.futures.tuple;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

public abstract class Tuple<T extends Tuple> {
  public static <T1, T2> Tuple2<T1, T2> tuple2() {
    return new Tuple2<>();
  }

  public static final <T extends Tuple<T>> Future<T> create(CompositeFuture compositeFuture, T tuple) {
    Future<T> future  = Future.future();
    compositeFuture.setHandler(ar -> {
      if (ar.succeeded()) {
        future.complete(tuple.set(ar.result()));
      } else {
        future.fail(ar.cause());
      }
    });
    return future;
  }

  public abstract T set(CompositeFuture result);
}
