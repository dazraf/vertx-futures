package io.dazraf.vertx.futures;

import io.dazraf.vertx.futures.consumer.Consumer2;
import io.dazraf.vertx.futures.function.Function2;
import io.dazraf.vertx.futures.tuple.Tuple2;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import org.slf4j.Logger;

import java.util.Arrays;

import static org.slf4j.LoggerFactory.*;

class FutureChain2<T1, T2> extends FutureChainN<Tuple2<T1, T2>, FutureChain2<T1, T2>> {
  private static final Logger LOG = getLogger(FutureChain2.class);

  public FutureChain2(Future<T1> future1, Future<T2> future2) {
    super(Arrays.asList(future1, future2));
  }

  public FutureChain2() {}

  public FutureChain2<T1, T2> peekSuccess(Consumer2<T1, T2> peekConsumer) {
    return super.peekSuccess(t -> t.accept(peekConsumer));
  }

  public FutureChain2<T1, T2> onSuccess(Consumer2<T1, T2> consumer) {
    return super.onSuccess(t -> t.accept(consumer));
  }

  public <R> FutureChain1<R> then(Function2<T1, T2, Future<R>> function) {
    return super.then(t -> t.apply(function));
  }

  @Override
  protected FutureChain2<T1, T2> create() {
    return new FutureChain2<>();
  }

  @Override
  protected Tuple2<T1, T2> createTuple(CompositeFuture resolvedFuture) {
    return new Tuple2<>(resolvedFuture);
  }

}
