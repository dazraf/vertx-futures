package io.dazraf.vertx.futures.processors;

import java.util.function.Function;

import io.dazraf.vertx.futures.function.Function2;
import io.dazraf.vertx.futures.function.Function3;
import io.dazraf.vertx.futures.function.Function4;
import io.dazraf.vertx.futures.tuple.Tuple2;
import io.dazraf.vertx.futures.tuple.Tuple3;
import io.dazraf.vertx.futures.tuple.Tuple4;
import io.vertx.core.AsyncResult;

import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;

/**
 * A {@link FutureProcessor} that maps successful values to a new type {@link R}
 */
public interface MapProcessor<T, R> extends FutureProcessor<T, R> {
  static <T, R> MapProcessor<T, R> mapResponse(Function<AsyncResult<T>, R> function) {
    return future -> succeededFuture(function.apply(future));
  }

  static <T, R> MapProcessor<T, R> map(Function<T, R> function) {
    return future -> succeededFuture(function.apply(future.result()));
  }

  static <T1, T2, R> MapProcessor<Tuple2<T1, T2>, R> map(Function2<T1, T2, R> function) {
    return future -> {
      if (future.succeeded()) {
        return succeededFuture(future.result().apply(function));
      } else {
        return failedFuture(future.cause());
      }
    };
  }

  static <T1, T2, T3, R> MapProcessor<Tuple3<T1, T2, T3>, R> map(Function3<T1, T2, T3, R> function) {
    return future -> {
      if (future.succeeded()) {
        return succeededFuture(future.result().apply(function));
      } else {
        return failedFuture(future.cause());
      }
    };
  }

  static <T1, T2, T3, T4, R> MapProcessor<Tuple4<T1, T2, T3, T4>, R> map(Function4<T1, T2, T3, T4, R> function) {
    return future -> {
      if (future.succeeded()) {
        return succeededFuture(future.result().apply(function));
      } else {
        return failedFuture(future.cause());
      }
    };
  }
}
