package io.dazraf.vertx.futures;

import io.dazraf.vertx.futures.processors.FutureProcessor;
import io.dazraf.vertx.tuple.Tuple;
import io.dazraf.vertx.tuple.Tuple2;
import io.dazraf.vertx.tuple.Tuple3;
import io.dazraf.vertx.tuple.Tuple4;
import io.dazraf.vertx.tuple.Tuple5;
import io.vertx.core.Future;

/**
 * An interface for chaining {@link Future}s
 */
public interface Futures<T> extends Future<T> {
  <R> Futures<R> then(FutureProcessor<T, R> processor);

  static <T> Futures<T> when(Future<T> future) {
    return FuturesImpl.when(future);
  }

  static <T1, T2> Futures<Tuple2<T1, T2>> when(Future<T1> f1,
                                               Future<T2> f2) {
    return FuturesImpl.when(Tuple.tuple(f1, f2));
  }

  static <T1, T2, T3> Futures<Tuple3<T1, T2, T3>> when(Future<T1> f1,
                                                       Future<T2> f2,
                                                       Future<T3> f3) {
    return FuturesImpl.when(Tuple.tuple(f1, f2, f3));
  }

  static <T1, T2, T3, T4> Futures<Tuple4<T1, T2, T3, T4>> when(Future<T1> f1,
                                                               Future<T2> f2,
                                                               Future<T3> f3,
                                                               Future<T4> f4) {
    return FuturesImpl.when(Tuple.tuple(f1, f2, f3, f4));
  }

  static <T1, T2, T3, T4, T5> Futures<Tuple5<T1, T2, T3, T4, T5>> when(Future<T1> f1,
                                                                       Future<T2> f2,
                                                                       Future<T3> f3,
                                                                       Future<T4> f4,
                                                                       Future<T5> f5) {
    return FuturesImpl.when(Tuple.tuple(f1, f2, f3, f4, f5));
  }
}
