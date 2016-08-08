package io.dazraf.vertx.futures;

import io.vertx.core.Future;

class FutureChain1<T> extends AbstractFutureChain<T, FutureChain1<T>> {
  FutureChain1(Future<T> result) {
    super(result);
  }

  @Override
  protected FutureChain1<T> wrap(Future<T> future) {
    return FutureChain.when(future);
  }
}
