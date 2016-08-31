package io.dazraf.vertx.futures.processors;

import org.slf4j.Logger;

import java.util.function.Function;

import io.dazraf.vertx.futures.function.Function2;
import io.dazraf.vertx.futures.function.Function3;
import io.dazraf.vertx.futures.function.Function4;
import io.dazraf.vertx.futures.tuple.Tuple2;
import io.dazraf.vertx.futures.tuple.Tuple3;
import io.dazraf.vertx.futures.tuple.Tuple4;
import io.dazraf.vertx.futures.Futures;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;

import static io.vertx.core.Future.failedFuture;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * A {@link FutureProcessor} that consumes the state of a {@link Futures} of type {@link T},
 * calls a delegate
 * and expects a result of type {@link Future} on type {@link R}
 * When used in a {@link Futures} chain, the chain becomes of a {@link Future} of type {@link R}
 */
public interface CallProcessor<T, R> extends FutureProcessor<T, R> {

  Logger LOG = getLogger(CallProcessor.class);

  static <T, R> CallProcessor<T, R> callOnResponse(Function<AsyncResult<T>, Future<R>> callFunction) {
    return future -> {
      try {
        return callFunction.apply(future);
      } catch (Throwable error) {
        LOG.error("call function failed");
        return failedFuture(error);
      }
    };
  }

  static <T, R> CallProcessor<T, R> call(Function<T, Future<R>> callFunction) {
    return callOnResponse(ar -> {
      if (ar.succeeded()) {
        return callFunction.apply(ar.result());
      } else {
        return failedFuture(ar.cause());
      }
    });
  }

  static <T1, T2, R> CallProcessor<Tuple2<T1, T2>, R> call(Function2<T1, T2, Future<R>> callFunction) {
    return call(tuple -> tuple.apply(callFunction));
  }

  static <T1, T2, T3, R> CallProcessor<Tuple3<T1, T2, T3>, R> call(Function3<T1, T2, T3, Future<R>> callFunction) {
    return call(tuple -> tuple.apply(callFunction));
  }

  static <T1, T2, T3, T4, R> CallProcessor<Tuple4<T1, T2, T3, T4>, R> call(Function4<T1, T2, T3, T4, Future<R>> callFunction) {
    return call(tuple -> tuple.apply(callFunction));
  }
}
