package io.dazraf.vertx.futures;

import io.dazraf.vertx.futures.consumer.Consumer3;
import io.dazraf.vertx.futures.function.Function3;
import io.dazraf.vertx.futures.tuple.Tuple3;
import io.dazraf.vertx.futures.tuple.Tuple2;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

import java.util.Arrays;
import java.util.function.Function;

public class FutureChain3<T1, T2, T3> extends FutureChainX<Tuple3<T1, T2, T3>, FutureChain3<T1, T2, T3>> {
  public FutureChain3(Future<T1> future1, Future<T2> future2, Future<T3> future3) {
    super(Arrays.asList(future1, future2, future3));
  }

  public FutureChain3(Object parent) {
    super(parent);
  }

  public FutureChain3<T1, T2, T3> peekSuccess(Consumer3<T1, T2, T3> peekConsumer) {
    return super.peekSuccess(t -> t.accept(peekConsumer));
  }

  public FutureChain3<T1, T2, T3> onSuccess(Consumer3<T1, T2, T3> consumer) {
    return super.onSuccess(t -> t.accept(consumer));
  }

  public <R1> FutureChain1<R1> then(Function3<T1, T2, T3, Future<R1>> function) {
    return super.then(t -> t.apply(function));
  }

  public <R1, R2> FutureChain2<R1, R2> then2(Function3<T1, T2, T3, Future<Tuple2<R1, R2>>> function) {
    return super.then2(t -> t.apply(function));
  }

  public <R1, R2, R3> FutureChain3<R1, R2, R3> then3(Function3<T1, T2, T3, Future<Tuple3<R1, R2, R3>>> function) {
    return super.then3(t -> t.apply(function));
  }

  public FutureChain3<T1, T2, T3> ifFailed3(Function<Throwable, Future<Tuple3<T1, T2, T3>>> ifFailedFn) {
    FutureChain3<T1, T2, T3> result = new FutureChain3<>(this);
    super.ifFailedX(result, ifFailedFn);
    return result;
  }

  @Override
  protected Tuple3<T1, T2, T3> createTuple(CompositeFuture resolvedFuture) {
    return new Tuple3<>(resolvedFuture);
  }

  @Override
  protected FutureChain3<T1, T2, T3> create() {
    return new FutureChain3<>(this);
  }
}
