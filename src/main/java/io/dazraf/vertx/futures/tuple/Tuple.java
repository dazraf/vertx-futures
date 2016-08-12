package io.dazraf.vertx.futures.tuple;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

public abstract class Tuple<T extends Tuple> {

  public static <T1, T2> Future<Tuple2<T1, T2>> all(Future<T1> future1, Future<T2> future2) {
    Future<Tuple2<T1, T2>> result = Future.future();
    CompositeFuture.all(future1, future2).setHandler(ar -> {
      if (ar.succeeded()) {
        result.complete(new Tuple2<>(ar.result()));
      } else {
        result.fail(ar.cause());
      }
    });
    return result;
  }

}
