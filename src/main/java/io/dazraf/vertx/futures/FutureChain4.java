package io.dazraf.vertx.futures;

import io.dazraf.vertx.futures.consumer.Consumer4;
import io.dazraf.vertx.futures.function.Function4;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

class FutureChain4<T1, T2, T3, T4> extends AbstractFutureChain<CompositeFuture, FutureChain4<T1, T2, T3, T4>> {
  FutureChain4(Future<T1> t1, Future<T2> t2, Future<T3> t3, Future<T4> t4) {
    this(CompositeFuture.all(t1, t2, t3, t4));
  }

  FutureChain4(Future<CompositeFuture> future) {
    super(future);
  }

  @Override
  protected FutureChain4<T1, T2, T3, T4> wrap(Future<CompositeFuture> future) {
    return new FutureChain4<>(future);
  }

  public <T5> FutureChain1<T5> then(Function4<T1, T2, T3, T4, Future<T5>> then) {
    return super.then(cf -> then.apply(getT1(cf), getT2(cf), getT3(cf), getT4(cf)));
  }

  public FutureChain4<T1, T2, T3, T4> onSuccess(Consumer4<T1, T2, T3, T4> consumer) {
    return super.onSuccess(cf -> consumer.accept(getT1(cf), getT2(cf), getT3(cf), getT4(cf)));
  }


  public FutureChain4<T1, T2, T3, T4> peek(Consumer4<T1, T2, T3, T4> consumer) {
    return super.peek(cf -> consumer.accept(getT1(cf), getT2(cf), getT3(cf), getT4(cf)));
  }

  private T4 getT4(CompositeFuture cf) {
    return cf.result(3);
  }

  private T3 getT3(CompositeFuture cf) {
    return cf.result(2);
  }

  private T2 getT2(CompositeFuture cf) {
    return cf.result(1);
  }

  private T1 getT1(CompositeFuture cf) {
    return cf.result(0);
  }
}
