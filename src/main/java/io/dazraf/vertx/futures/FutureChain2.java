package io.dazraf.vertx.futures;

import io.dazraf.vertx.futures.consumer.Consumer2;
import io.dazraf.vertx.futures.function.Function2;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

class FutureChain2<T1, T2> extends AbstractFutureChain<CompositeFuture, FutureChain2<T1, T2>> {
  FutureChain2(Future<T1> t1, Future<T2> t2) {
    this(CompositeFuture.all(t1, t2));
  }

  FutureChain2(Future<CompositeFuture> tuple) {
    super(tuple);
  }

  @Override
  protected FutureChain2<T1, T2> wrap(Future<CompositeFuture> result) {
    return new FutureChain2<>(result);
  }

  <T3> FutureChain1<T3> then(Function2<T1, T2, Future<T3>> then) {
    return super.then(cf -> then.apply(getT1(cf), getT2(cf)));
  }

  FutureChain2<T1, T2> onSuccess(Consumer2<T1, T2> consumer) {
    return super.onSuccess(cf -> consumer.accept(getT1(cf), getT2(cf)));
  }

  public FutureChain2<T1, T2> peek(Consumer2<T1, T2> consumer) {
    return super.peek(cf -> consumer.accept(getT1(cf), getT2(cf)));
  }

  private T2 getT2(CompositeFuture cf) {
    return cf.result(1);
  }

  private T1 getT1(CompositeFuture cf) {
    return cf.result(0);
  }
}
