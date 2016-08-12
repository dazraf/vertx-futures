package io.dazraf.vertx.futures;

import io.vertx.core.Future;

class FutureChain1<T> extends FutureChainImpl<T, FutureChain1<T>> {
  FutureChain1() {}

  public FutureChain1(Future<T> future) {
    future.setHandler(this);
  }

  @Override
  protected FutureChain1<T> create() {
    return new FutureChain1<>();
  }

}
