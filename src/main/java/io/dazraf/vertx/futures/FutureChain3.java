package io.dazraf.vertx.futures;

import io.dazraf.vertx.futures.consumer.Consumer3;
import io.dazraf.vertx.futures.function.Function3;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

class FutureChain3<T1, T2, T3> extends AbstractFutureChain<CompositeFuture, FutureChain3<T1, T2, T3>> {
  FutureChain3(Future<T1> t1, Future<T2> t2, Future<T3> t3) {
    this(CompositeFuture.all(t1, t2, t3));
  }

  FutureChain3(Future<CompositeFuture> future) {
    super(future);
  }

  @Override
  protected FutureChain3<T1, T2, T3> wrap(Future<CompositeFuture> future) {
    return new FutureChain3<>(future);
  }

  public <T4> FutureChain1<T4> then(Function3<T1, T2, T3, Future<T4>> then) {
    return super.then(cf -> then.apply(getT1(cf), getT2(cf), getT3(cf)));
  }

  public FutureChain3<T1, T2, T3> onSuccess(Consumer3<T1, T2, T3> consumer) {
    return super.onSuccess(cf -> consumer.accept(getT1(cf), getT2(cf), getT3(cf)));
  }

  public FutureChain3<T1, T2, T3> peek(Consumer3<T1, T2, T3> consumer) {
    return super.peek(cf -> consumer.accept(getT1(cf), getT2(cf), getT3(cf)));
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
