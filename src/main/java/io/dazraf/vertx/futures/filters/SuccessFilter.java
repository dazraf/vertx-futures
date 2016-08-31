package io.dazraf.vertx.futures.filters;

import java.util.function.Consumer;

import io.dazraf.vertx.futures.consumer.Consumer2;
import io.dazraf.vertx.futures.consumer.Consumer3;
import io.dazraf.vertx.futures.consumer.Consumer4;
import io.dazraf.vertx.futures.tuple.Tuple2;
import io.dazraf.vertx.futures.tuple.Tuple3;
import io.dazraf.vertx.futures.tuple.Tuple4;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * Filters the state of a Future for successful completion, calling a handler function
 */
public interface SuccessFilter<T> extends Handler<AsyncResult<T>> {
  static <T> SuccessFilter<T> success(Consumer<T> consumer) {
    return result -> {
      if (result.succeeded()) {
        consumer.accept(result.result());
      }
    };
  }

  static <T> Handler<AsyncResult<T>> success(Runnable runnable) {
    return success(result -> runnable.run());
  }


  static <T1, T2> Handler<AsyncResult<Tuple2<T1, T2>>> success(Consumer2<T1, T2> consumer) {
    return success(tuple -> tuple.accept(consumer));
  }

  static <T1, T2, T3> Handler<AsyncResult<Tuple3<T1, T2, T3>>> success(Consumer3<T1, T2, T3> consumer) {
    return success(tuple -> tuple.accept(consumer));
  }

  static <T1, T2, T3, T4> Handler<AsyncResult<Tuple4<T1, T2, T3, T4>>> success(Consumer4<T1, T2, T3, T4> consumer) {
    return success(tuple -> tuple.accept(consumer));
  }


}
