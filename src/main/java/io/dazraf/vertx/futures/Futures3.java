package io.dazraf.vertx.futures;

import io.dazraf.vertx.futures.consumer.Consumer3;
import io.dazraf.vertx.futures.function.Function3;
import io.dazraf.vertx.futures.tuple.Tuple2;
import io.dazraf.vertx.futures.tuple.Tuple3;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

import java.util.Arrays;
import java.util.function.Function;

public class Futures3<T1, T2, T3> extends FuturesX<Tuple3<T1, T2, T3>, Futures3<T1, T2, T3>> {
  Futures3(Future<T1> future1, Future<T2> future2, Future<T3> future3) {
    super(Arrays.asList(future1, future2, future3));
  }

  Futures3(Object parent) {
    super(parent);
  }

  public Futures3<T1, T2, T3> peekSuccess(Consumer3<T1, T2, T3> peekConsumer) {
    return super.peekSuccess(t -> t.accept(peekConsumer));
  }

  public Futures3<T1, T2, T3> onSuccess(Consumer3<T1, T2, T3> consumer) {
    return super.onSuccess(t -> t.accept(consumer));
  }

  public <R1> Futures1<R1> then(Function3<T1, T2, T3, Future<R1>> function) {
    return super.then(t -> t.apply(function));
  }

  public <R1, R2> Futures2<R1, R2> then2(Function3<T1, T2, T3, Future<Tuple2<R1, R2>>> function) {
    return super.then2(t -> t.apply(function));
  }

  public <R1, R2, R3> Futures3<R1, R2, R3> then3(Function3<T1, T2, T3, Future<Tuple3<R1, R2, R3>>> function) {
    return super.then3(t -> t.apply(function));
  }

  public Futures3<T1, T2, T3> ifFailed3(Function<Throwable, Future<Tuple3<T1, T2, T3>>> ifFailedFn) {
    Futures3<T1, T2, T3> result = new Futures3<>(this);
    super.ifFailedX(result, ifFailedFn);
    return result;
  }

  public <R1> Futures1<R1> map(Function3<T1, T2, T3, R1> function) {
    return super.map(t -> t.apply(function));
  }

  public <R1, R2> Futures2<R1, R2> map2(Function3<T1, T2, T3, Tuple2<R1, R2>> function) {
    return super.map2(t -> t.apply(function));
  }
  public <R1, R2, R3> Futures3<R1, R2, R3> map3(Function3<T1, T2, T3, Tuple3<R1, R2, R3>> function) {
    return super.map3(t -> t.apply(function));
  }

  @Override
  protected Tuple3<T1, T2, T3> createTuple(CompositeFuture resolvedFuture) {
    return new Tuple3<>(resolvedFuture);
  }

  @Override
  protected Futures3<T1, T2, T3> create() {
    return new Futures3<>(this);
  }
}
