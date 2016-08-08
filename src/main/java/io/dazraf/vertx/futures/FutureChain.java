package io.dazraf.vertx.futures;

import io.dazraf.vertx.futures.tuple.Tuple2;
import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @param <T>
 * @param <Derived> The type of class that actually implements this interface
 */
public interface FutureChain<T, Derived extends FutureChain<T, Derived>> extends Future<T> {

  <R> FutureChain1<R> then(Function<T, Future<R>> futureFunction);
  <T1, T2> FutureChain2<T1, T2> then(Function<T, Future<CompositeFuture>> futureFunction,
                                     Class<T1> class1, Class<T2> class2);
  <T1, T2, T3> FutureChain3<T1, T2, T3> then(Function<T, Future<CompositeFuture>> futureFunction,
                                             Class<T1> class1, Class<T2> class2, Class<T3> class3);
  <T1, T2, T3, T4> FutureChain4<T1, T2, T3, T4> then(Function<T, Future<CompositeFuture>> futureFunction,
                                             Class<T1> class1, Class<T2> class2, Class<T3> class3, Class<T4> class4);


  <T1, T2> FutureChain2<T1, T2> then (Function<T, Future<CompositeFuture>> futureFunction, Tuple2<T1, T2> collector);
  Derived onSuccess(Consumer<T> consumer);
  Derived onError(Consumer<Throwable> consumer);
  Derived peek(Consumer<T> consumer);
  Derived onResult(Consumer<AsyncResult<T>> consumer);
  FutureChain1<Void> mapVoid();

  static <T> FutureChain1<T> when(Future<T> result) {
    if (result instanceof FutureChain1) {
      return (FutureChain1<T>)result;
    } else {
      return new FutureChain1<>(result);
    }
  }


  // two futures
  static <T1, T2> FutureChain2<T1, T2> when(Future<T1> t1, Future<T2> t2) {
    return new FutureChain2<>(t1, t2);
  }
  static <T1, T2> FutureChain2<T1, T2> when(FutureChain2<T1, T2> future) { return future; }
  static <T1, T2> FutureChain2<T1, T2> when(Future<CompositeFuture> composite, Class<T1> class1, Class<T2> class2) {
    return new FutureChain2<>(composite);
  }


  // three futures
  static <T1, T2, T3> FutureChain3<T1, T2, T3> when(Future<T1> t1, Future<T2> t2, Future<T3> t3) {
    return new FutureChain3<>(t1, t2, t3);
  }
  static <T1, T2, T3> FutureChain3<T1, T2, T3> when(FutureChain3<T1, T2, T3> future) { return future; }
  static <T1, T2, T3> FutureChain3<T1, T2, T3> when(Future<CompositeFuture> composite, Class<T1> class1, Class<T2> class2, Class<T3> class3) {
    return new FutureChain3<>(composite);
  }

  // four futures
  static <T1, T2, T3, T4> FutureChain4<T1, T2, T3, T4> when(Future<T1> t1, Future<T2> t2, Future<T3> t3, Future<T4> t4) {
    return new FutureChain4<>(t1, t2, t3, t4);
  }
  static <T1, T2, T3, T4> FutureChain4<T1, T2, T3, T4> when(FutureChain4<T1, T2, T3, T4> future) { return future; }
  static <T1, T2, T3, T4> FutureChain4<T1, T2, T3, T4> when(Future<CompositeFuture> composite,
                                                            Class<T1> class1, Class<T2> class2, Class<T3> class3, Class<T4> class4) {
    return new FutureChain4<>(composite);
  }
}
