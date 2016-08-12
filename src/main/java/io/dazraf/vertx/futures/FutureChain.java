package io.dazraf.vertx.futures;

import io.dazraf.vertx.futures.tuple.Tuple2;
import io.dazraf.vertx.futures.tuple.Tuple3;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @param <T>
 * @param <Derived> The type of class that actually implements this interface
 */
public interface FutureChain<T, Derived extends FutureChain<T, Derived>> extends Future<T> {

  // receive the result, error, or both
  // the given state is passed through to the subsequent listeners in the graph
  // if the consumer throws, the error is propagated
  Derived onSuccess(Consumer<T> consumer);
  Derived onFail(Consumer<Throwable> consumer);
  Derived onComplete(Consumer<AsyncResult<T>> consumer);

  // receive the result, error, or both
  // the given state is passed through the subsequent listeners in the graph
  // if the consumer throws, the exception is swallowed and not reported
  Derived peekSuccess(Consumer<T> consumer);
  Derived peekFail(Consumer<Throwable> consumer);
  Derived peekComplete(Consumer<AsyncResult<T>> consumer);

  // given a success, return another future
  <R> FutureChain1<R> then(Function<T, Future<R>> thenFn);
  <T1, T2> FutureChain2<T1, T2> then2(Function<T, Future<Tuple2<T1, T2>>> thenFn);
  <T1, T2, T3> FutureChain3<T1, T2, T3> then3(Function<T, Future<Tuple3<T1, T2, T3>>> thenFn);

  // if failed, then return another future
  FutureChain1<T> ifFailed(Function<Throwable, Future<T>> whenFn);

  FutureChain1<Void> mapVoid();

  // --- Factory methods
  static <T> FutureChain1<T> when(Future<T> future) {
    return new FutureChain1<>(future);
  }

  static <T1, T2> FutureChain2<T1, T2> when(Future<T1> future1, Future<T2> future2) {
    return new FutureChain2<>(future1, future2);
  }

  static <T1, T2, T3> FutureChain3<T1, T2, T3> when(Future<T1> future1, Future<T2> future2, Future<T3> future3) {
    return new FutureChain3<>(future1, future2, future3);
  }
}
