package io.dazraf.vertx.futures;

import io.dazraf.vertx.futures.tuple.Tuple2;
import io.dazraf.vertx.futures.tuple.Tuple3;
import io.dazraf.vertx.futures.tuple.Tuple4;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This is the primary interface for working with these futures.
 * It provides the following method categories:
 *
 * <ul>
 *
 *   <li>Factories: these are in the form {@link #when(Future)}, {@link #when(Future, Future)} etc </li>
 *
 *   <li>Chaining methods: these allow chaining of functions that return {@link Future}.
 *    They are in the form {@link #then(Function)}, {@link #then2(Function)} etc</li>
 *
 *   <li>Event handlers: {@link #onSuccess} {@link #onFail} {@link #onComplete}.
 *
 *   Exceptions from these handlers affect the computation graph.
 *   There are also specialised forms of these that can de-structure composite futures. More about this later.</li>
 *
 *   <li>Peek handlers: {@link #peekSuccess(Consumer)} {@link #peekFail(Consumer)} {@link #peekComplete(Consumer)}
 *
 *   Exceptions from these handlers do not affect the computation graph.
 *   There are also specialised forms of these that can de-structure composite futures. More about this later.</li>
 *
 *   <li><code>map</code> functions.</li>
 *
 *   <li>{@link #ifFailed(Function)} for conditional handling of flow when a future has failed.</li>
 * </ul>
 *
 * It provides a set of factory methods: {@link Futures#when(Future)}
 * @param <T> The result type of the future
 * @param <Derived> The type of class that actually implements this interface
 */
public interface Futures<T, Derived extends Futures<T, Derived>> extends Future<T> {

  // receive the result, error, or both
  // the given state is passed through to the subsequent listeners in the graph
  // if the consumer throws, the error is propagated to all listeners
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
  <R> Futures1<R> then(Function<T, Future<R>> thenFn);
  <T1, T2> Futures2<T1, T2> then2(Function<T, Future<Tuple2<T1, T2>>> thenFn);
  <T1, T2, T3> Futures3<T1, T2, T3> then3(Function<T, Future<Tuple3<T1, T2, T3>>> thenFn);
  <T1, T2, T3, T4> Futures4<T1, T2, T3, T4> then4(Function<T, Future<Tuple4<T1, T2, T3, T4>>> thenFn);

  // if failed, then return another future
  Futures1<T> ifFailed(Function<Throwable, Future<T>> whenFn);

  Futures1<Void> mapVoid();

  <R> Futures1<R> map(R value);
  <R> Futures1<R> map(Function<T, R> mapFn);
  <T1, T2> Futures2<T1, T2> map2(Function<T, Tuple2<T1, T2>> mapFn);
  <T1, T2, T3> Futures3<T1, T2, T3> map3(Function<T, Tuple3<T1, T2, T3>> mapFn);
  <T1, T2, T3, T4> Futures4<T1, T2, T3, T4> map4(Function<T, Tuple4<T1, T2, T3, T4>> mapFn);


  // --- Factory methods - all of the form when
  static <T> Futures1<T> when(Future<T> future) {
    return new Futures1<>(future);
  }

  static <T1, T2> Futures2<T1, T2> when(Future<T1> future1, Future<T2> future2) {
    return new Futures2<>(future1, future2);
  }

  static <T1, T2, T3> Futures3<T1, T2, T3> when(Future<T1> future1, Future<T2> future2, Future<T3> future3) {
    return new Futures3<>(future1, future2, future3);
  }


  // --- Default methods

  default Derived onSuccess(Runnable runnable) { return onSuccess(t -> runnable.run()); }
  default Derived onFail(Runnable runnable) { return onFail(e -> runnable.run()); }
  default Derived onComplete(Runnable runnable) { return onComplete(ar -> runnable.run()); }

  default Derived peekSuccess(Runnable runnable) { return peekSuccess(t -> runnable.run()); }
  default Derived peekFail(Runnable runnable) { return peekFail(t -> runnable.run()); }
  default Derived peekComplete(Runnable runnable) { return peekComplete(t -> runnable.run()); }
  default Futures1<T> ifFailed(Supplier<Future<T>> supplier) { return ifFailed(t -> supplier.get());}

}
