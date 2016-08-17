package io.dazraf.vertx.futures;

import io.dazraf.vertx.futures.consumer.Consumer2;
import io.dazraf.vertx.futures.function.Function2;
import io.dazraf.vertx.futures.tuple.Tuple2;
import io.dazraf.vertx.futures.tuple.Tuple3;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.function.Function;

import static org.slf4j.LoggerFactory.*;

public class Futures2<T1, T2> extends FuturesX<Tuple2<T1, T2>, Futures2<T1, T2>> {
  private static final Logger LOG = getLogger(Futures2.class);

  Futures2(Future<T1> future1, Future<T2> future2) {
    super(Arrays.asList(future1, future2));
  }

  Futures2(Object parent) {
    super(parent);
  }

  public Futures2<T1, T2> peekSuccess(Consumer2<T1, T2> peekConsumer) {
    return super.peekSuccess(t -> t.accept(peekConsumer));
  }

  public Futures2<T1, T2> onSuccess(Consumer2<T1, T2> consumer) {
    return super.onSuccess(t -> t.accept(consumer));
  }

  public <R1> Futures1<R1> map(Function2<T1, T2, R1> function) {
    return super.map(t -> t.apply(function));
  }

  public <R1, R2> Futures2<R1, R2> map2(Function2<T1, T2, Tuple2<R1, R2>> function) {
    return super.map2(t -> t.apply(function));
  }
  public <R1, R2, R3> Futures3<R1, R2, R3> map3(Function2<T1, T2, Tuple3<R1, R2, R3>> function) {
    return super.map3(t -> t.apply(function));
  }

  public <R1> Futures1<R1> then(Function2<T1, T2, Future<R1>> function) {
    return super.then(t -> t.apply(function));
  }

  public <R1, R2> Futures2<R1, R2> then2(Function2<T1, T2, Future<Tuple2<R1, R2>>> function) {
    return super.then2(t -> t.apply(function));
  }

  public <R1, R2, R3> Futures3<R1, R2, R3> then3(Function2<T1, T2, Future<Tuple3<R1, R2, R3>>> function) {
    return super.then3(t -> t.apply(function));
  }

  public Futures2<T1, T2> ifFailed2(Function<Throwable, Future<Tuple2<T1, T2>>> ifFailedFn) {
    Futures2<T1, T2> result = new Futures2<>(this);
    super.ifFailedX(result, ifFailedFn);
    return result;
  }

  @Override
  protected Futures2<T1, T2> create() {
    return new Futures2<>(this);
  }

  @Override
  protected Tuple2<T1, T2> createTuple(CompositeFuture resolvedFuture) {
    return new Tuple2<>(resolvedFuture);
  }

}
