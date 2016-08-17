package io.dazraf.vertx.futures;

import io.dazraf.vertx.futures.consumer.Consumer4;
import io.dazraf.vertx.futures.function.Function4;
import io.dazraf.vertx.futures.tuple.Tuple2;
import io.dazraf.vertx.futures.tuple.Tuple3;
import io.dazraf.vertx.futures.tuple.Tuple4;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

import java.util.Arrays;
import java.util.function.Function;

public class Futures4<T1, T2, T3, T4> extends FuturesX<Tuple4<T1, T2, T3, T4>, Futures4<T1, T2, T3, T4>> {
  Futures4(Future<T1> future1, Future<T2> future2, Future<T3> future3, Future<T4> future4) {
    super(Arrays.asList(future1, future2, future3, future4));
  }

  Futures4(Object parent) {
    super(parent);
  }

  public Futures4<T1, T2, T3, T4> peekSuccess(Consumer4<T1, T2, T3, T4> peekConsumer) {
    return super.peekSuccess(t -> t.accept(peekConsumer));
  }

  public Futures4<T1, T2, T3, T4> onSuccess(Consumer4<T1, T2, T3, T4> consumer) {
    return super.onSuccess(t -> t.accept(consumer));
  }

  public <R1> Futures1<R1> then(Function4<T1, T2, T3, T4, Future<R1>> function) {
    return super.then(t -> t.apply(function));
  }

  public <R1, R2> Futures2<R1, R2> then2(Function4<T1, T2, T3, T4, Future<Tuple2<R1, R2>>> function) {
    return super.then2(t -> t.apply(function));
  }

  public <R1, R2, R3> Futures3<R1, R2, R3> then3(Function4<T1, T2, T3, T4, Future<Tuple3<R1, R2, R3>>> function) {
    return super.then3(t -> t.apply(function));
  }

  public <R1, R2, R3, R4> Futures4<R1, R2, R3, R4> then4(Function4<T1, T2, T3, T4, Future<Tuple4<R1, R2, R3, R4>>> function) {
    return super.then4(t -> t.apply(function));
  }

  public Futures4<T1, T2, T3, T4> ifFailed4(Function<Throwable, Future<Tuple4<T1, T2, T3, T4>>> ifFailedFn) {
    Futures4<T1, T2, T3, T4> result = new Futures4<>(this);
    super.ifFailedX(result, ifFailedFn);
    return result;
  }

  public <R1> Futures1<R1> map(Function4<T1, T2, T3, T4, R1> function) {
    return super.map(t -> t.apply(function));
  }

  public <R1, R2> Futures2<R1, R2> map2(Function4<T1, T2, T3, T4, Tuple2<R1, R2>> function) {
    return super.map2(t -> t.apply(function));
  }
  public <R1, R2, R3> Futures3<R1, R2, R3> map3(Function4<T1, T2, T3, T4, Tuple3<R1, R2, R3>> function) {
    return super.map3(t -> t.apply(function));
  }

  public <R1, R2, R3, R4> Futures4<R1, R2, R3, R4> map4(Function4<T1, T2, T3, T4, Tuple4<R1, R2, R3, R4>> function) {
    return super.map4(t -> t.apply(function));
  }

  @Override
  protected Tuple4<T1, T2, T3, T4> createTuple(CompositeFuture resolvedFuture) {
    return new Tuple4<>(resolvedFuture);
  }

  @Override
  protected Futures4<T1, T2, T3, T4> create() {
    return new Futures4<>(this);
  }
}
