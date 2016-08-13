package io.dazraf.vertx.futures;

import io.vertx.core.Future;

public class FutureChain1<T> extends FutureChainImpl<T, FutureChain1<T>> {
  FutureChain1(Object parent) {
    super(parent);
  }

  FutureChain1(Future<T> future) {
    super(future);
    future.setHandler(this);
  }

  @Override
  protected FutureChain1<T> create() {
    return new FutureChain1<>((Object)this);
  }

}
