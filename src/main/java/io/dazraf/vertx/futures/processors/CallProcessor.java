package io.dazraf.vertx.futures.processors;

import io.dazraf.vertx.futures.Futures;
import io.dazraf.vertx.function.Function2;
import io.dazraf.vertx.function.Function3;
import io.dazraf.vertx.function.Function4;
import io.dazraf.vertx.tuple.Tuple2;
import io.dazraf.vertx.tuple.Tuple3;
import io.dazraf.vertx.tuple.Tuple4;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.vertx.core.Future.*;
import static java.util.stream.Collectors.*;
import static org.slf4j.LoggerFactory.*;

/**
 * Functions for creating {@code call} processor.
 * The semantics of {@code call} is to pass the state of the chain (of type {@link T})
 * to a function that returns a {@link Future} of type {@link R}
 */
public interface CallProcessor<T, R> extends FutureProcessor<T, R> {

  Logger LOG = getLogger(CallProcessor.class);

  /**
   * Receive the state of the chain as a {@code AsyncResult<T>}, call the function {@code callFunction}
   * which returns {@code Future<R>}
   * @param callFunction the function to be called with the state of the chain
   * @param <T> the type for the chain preceding this processor
   * @param <R> the type of result returned by {@code callFunction} and that of the chain after this processor
   * @return the processor
   */
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

  /**
   * Given a successfully resolved chain, calls {@code supplier} which returns the next future for the chain {@code Future<R>}
   * @param supplier
   * @param <T> the type for the chain preceding this processor
   * @param <R> the type of result returned by {@code supplier} and that of the chain after this processor
   * @return the processor
   */
  static <T, R> CallProcessor<T, R> call(Supplier<Future<R>> supplier) {
    return call(result -> supplier.get());
  }

  /**
   * Given a successfully resolved chain, receive the result {@code T} of the chain, which then calls {@code callFunction}
   * which returns {@code Future<R>}
   * @param callFunction the function to be called with the result of the chain
   * @param <T> the type for the chain preceding this processor
   * @param <R> the type of result returned by {@code callFunction} and that of the chain after this processor
   * @return the processor
   */
  static <T, R> CallProcessor<T, R> call(Function<T, Future<R>> callFunction) {
    return callOnResponse(ar -> {
      if (ar.succeeded()) {
        return callFunction.apply(ar.result());
      } else {
        return failedFuture(ar.cause());
      }
    });
  }

  /**
   * Given a succesfully resolved chain, producing {@code List<T>}, receive the result and pass each element to
   * the function {@code flatMapFunction} which in turn returns a {@code Future<R>}.
   * The resulting {@code List<Future<R>>} is transformed and returned as {@code Future<List<R>>}
   * @param flatMapFunction the function that receives each element of the chain result {@code List<T>}
   *                        and for each returns {@code Future<R>}
   * @param <InputCollection> the type of the incoming list type
   * @param <T> the type of the elements in the chain result {@code List}
   * @param <R> the type of result returned by {@code callFunction}
   * @return the processor
   */
  static <InputCollection extends Collection<T>, T, R> CallProcessor<InputCollection, List<R>> flatMap(Function<T, Future<R>> flatMapFunction) {
    return callOnResponse(ar -> {
        if (ar.failed()) {
          return failedFuture(ar.cause());
        } else {
          final List<Future> collect = ar.result().stream().map(flatMapFunction).collect(toList());
          Future<List<R>> result = future();
          CompositeFuture.all(collect)
            .setHandler(acf -> {
              if (acf.succeeded()) {
                result.complete(acf.result().<R>list());
              } else {
                result.fail(acf.cause());
              }
            });
          return result;
        }
      }
    );
  }

  /**
   * A {@code call} operator on a chain emitting a {@link Tuple2}, destructuring the tuple before calling
   * {@code callFunction}. As other {@code call} operators, this function returns a {@code Future<R>} which
   * forms the next step in the {@link Futures} chain.
   * @param callFunction the function to be called with the destructured result of the chain
   * @param <T1> the type of the first element of the tuple, emitted by the chain preceding this processor
   * @param <T2> the type of the second element of the tuple, emitted by the chain preceding this processor
   * @param <R> the type of result returned by {@code callFunction} and that of the chain after this processor
   * @return the processor
   */
  static <T1, T2, R> CallProcessor<Tuple2<T1, T2>, R> call(Function2<T1, T2, Future<R>> callFunction) {
    return call(tuple -> tuple.apply(callFunction));
  }

  /**
   * A {@code call} operator on a chain emitting a {@link Tuple3}, destructuring the tuple before calling
   * {@code callFunction}. As other {@code call} operators, this function returns a {@code Future<R>} which
   * forms the next step in the {@link Futures} chain.
   * @param callFunction the function to be called with the destructured result of the chain
   * @param <T1> the type of the first element of the tuple, emitted by the chain preceding this processor
   * @param <T2> the type of the second element of the tuple, emitted by the chain preceding this processor
   * @param <T3> the type of the third element of the tuple, emitted by the chain preceding this processor
   * @param <R> the type of result returned by {@code callFunction} and that of the chain after this processor
   * @return the processor
   */
  static <T1, T2, T3, R> CallProcessor<Tuple3<T1, T2, T3>, R> call(Function3<T1, T2, T3, Future<R>> callFunction) {
    return call(tuple -> tuple.apply(callFunction));
  }

  /**
   * A {@code call} operator on a chain emitting a {@link Tuple4}, destructuring the tuple before calling
   * {@code callFunction}. As other {@code call} operators, this function returns a {@code Future<R>} which
   * forms the next step in the {@link Futures} chain.
   * @param callFunction the function to be called with the destructured result of the chain
   * @param <T1> the type of the first element of the tuple, emitted by the chain preceding this processor
   * @param <T2> the type of the second element of the tuple, emitted by the chain preceding this processor
   * @param <T3> the type of the third element of the tuple, emitted by the chain preceding this processor
   * @param <T4> the type of the fourth element of the tuple, emitted by the chain preceding this processor
   * @param <R> the type of result returned by {@code callFunction} and that of the chain after this processor
   * @return the processor
   */
  static <T1, T2, T3, T4, R> CallProcessor<Tuple4<T1, T2, T3, T4>, R> call(Function4<T1, T2, T3, T4, Future<R>> callFunction) {
    return call(tuple -> tuple.apply(callFunction));
  }
}
