package io.dazraf.vertx.futures.tuple;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public abstract class Tuple<T extends Tuple> {

  // PUBLIC - value collectors

  public static <T1, T2> Tuple2<T1, T2> all(T1 t1, T2 t2) {
    return new Tuple2<>(t1, t2);
  }

  public static <T1, T2, T3> Tuple3<T1, T2, T3> all(T1 t1, T2 t2, T3 t3) {
    return new Tuple3<>(t1, t2, t3);
  }

  // PUBLIC - future collectors

  public static <T1, T2> Future<Tuple2<T1, T2>> all(Future<T1> f1, Future<T2> f2) {
    return allFutureX(Arrays.asList(f1, f2), Tuple2::new);
  }

  public static <T1, T2, T3> Future<Tuple3<T1, T2, T3>> all(Future<T1> f1, Future<T2> f2, Future<T3> f3) {
    return allFutureX(Arrays.asList(f1, f2, f3), Tuple3::new);
  }



  private static <T extends Tuple<T>> Future<T> allFutureX(List<Future> futures, Function<CompositeFuture, T> adapter) {
    Future<T> result = Future.future();
    CompositeFuture.all(futures).setHandler(ar -> {
      if (ar.succeeded()) {
        result.complete(adapter.apply(ar.result()));
      } else {
        result.fail(ar.cause());
      }
    });
    return result;
  }
}
