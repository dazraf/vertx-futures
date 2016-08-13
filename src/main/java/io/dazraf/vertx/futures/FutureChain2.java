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

public class FutureChain2<T1, T2> extends FutureChainX<Tuple2<T1, T2>, FutureChain2<T1, T2>> {
  private static final Logger LOG = getLogger(FutureChain2.class);

  FutureChain2(Future<T1> future1, Future<T2> future2) {
    super(Arrays.asList(future1, future2));
  }

  FutureChain2(Object parent) {
    super(parent);
  }

  public FutureChain2<T1, T2> peekSuccess(Consumer2<T1, T2> peekConsumer) {
    return super.peekSuccess(t -> t.accept(peekConsumer));
  }

  public FutureChain2<T1, T2> onSuccess(Consumer2<T1, T2> consumer) {
    return super.onSuccess(t -> t.accept(consumer));
  }

  public <R1> FutureChain1<R1> then(Function2<T1, T2, Future<R1>> function) {
    return super.then(t -> t.apply(function));
  }

  public <R1, R2> FutureChain2<R1, R2> then2(Function2<T1, T2, Future<Tuple2<R1, R2>>> function) {
    return super.then2(t -> t.apply(function));
  }

  public <R1, R2, R3> FutureChain3<R1, R2, R3> then3(Function2<T1, T2, Future<Tuple3<R1, R2, R3>>> function) {
    return super.then3(t -> t.apply(function));
  }

  public FutureChain2<T1, T2> ifFailed2(Function<Throwable, Future<Tuple2<T1, T2>>> ifFailedFn) {
    FutureChain2<T1, T2> result = new FutureChain2<>(this);
    super.ifFailedX(result, ifFailedFn);
    return result;
  }

  @Override
  protected FutureChain2<T1, T2> create() {
    return new FutureChain2<>(this);
  }

  @Override
  protected Tuple2<T1, T2> createTuple(CompositeFuture resolvedFuture) {
    return new Tuple2<>(resolvedFuture);
  }

}
