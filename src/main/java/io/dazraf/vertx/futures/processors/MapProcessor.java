package io.dazraf.vertx.futures.processors;

import java.util.function.Function;

import io.dazraf.vertx.function.Function2;
import io.dazraf.vertx.function.Function3;
import io.dazraf.vertx.function.Function4;
import io.dazraf.vertx.function.Function5;
import io.dazraf.vertx.tuple.Tuple2;
import io.dazraf.vertx.tuple.Tuple3;
import io.dazraf.vertx.tuple.Tuple4;
import io.dazraf.vertx.tuple.Tuple5;
import io.vertx.core.AsyncResult;

import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;

/**
 * A {@link FutureProcessor} that maps successful values to a new type {@link R}
 */
public interface MapProcessor<T, R> extends FutureProcessor<T, R> {
  static <T, R> MapProcessor<T, R> mapOnResponse(Function<AsyncResult<T>, R> function) {
    return future -> {
      try {
        return succeededFuture(function.apply(future));
      }
      catch (WrappedException err) {
        return failedFuture(err.getCause());
      }
      catch (Throwable err) {
        return failedFuture(err);
      }
    };
  }


  static <T, R> MapProcessor<T, R> map(Function<T, R> function) {
    return mapOnResponse(future -> {
      if (future.succeeded()) {
        return function.apply(future.result());
      } else {
        throw new WrappedException(future.cause());
      }
    });
  }

  static <T1, T2, R> MapProcessor<Tuple2<T1, T2>, R> map(Function2<T1, T2, R> function) {
    return map(tuple -> tuple.apply(function));
  }

  static <T1, T2, T3, R> MapProcessor<Tuple3<T1, T2, T3>, R> map(Function3<T1, T2, T3, R> function) {
    return map(tuple -> tuple.apply(function));
  }

  static <T1, T2, T3, T4, R> MapProcessor<Tuple4<T1, T2, T3, T4>, R> map(Function4<T1, T2, T3, T4, R> function) {
    return map(tuple -> tuple.apply(function));
  }

  static <T1, T2, T3, T4, T5, R> MapProcessor<Tuple5<T1, T2, T3, T4, T5>, R> map(Function5<T1, T2, T3, T4, T5, R> function) {
    return map(tuple -> tuple.apply(function));
  }
}
