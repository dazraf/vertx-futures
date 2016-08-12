package io.dazraf.vertx.futures;

import io.dazraf.vertx.futures.tuple.Tuple2;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @param <T>
 * @param <Derived> The type of class that actually implements this interface
 */
public interface FutureChain<T, Derived extends FutureChain<T, Derived>> extends Future<T> {

  <R> FutureChain1<R> then(Function<T, Future<R>> futureFunction);

  Derived onSuccess(Consumer<T> consumer);

  Derived peekSuccess(Consumer<T> consumer);

  Derived onError(Consumer<Throwable> consumer);

  Derived onResult(Consumer<AsyncResult<T>> consumer);

  <T1, T2> FutureChain2<T1, T2> then2(Function<T, Future<Tuple2<T1, T2>>> futureFunction);
//
//  FutureChain1<Void> mapVoid();

  static <T> FutureChain1<T> when(Future<T> future) {
    return new FutureChain1<>(future);
  }

  static <T1, T2> FutureChain2<T1, T2> when(Future<T1> future1, Future<T2> future2) {
    return new FutureChain2<>(future1, future2);
  }
}
