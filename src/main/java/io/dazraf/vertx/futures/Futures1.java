package io.dazraf.vertx.futures;

import io.vertx.core.Future;

public class Futures1<T> extends FuturesImpl<T, Futures1<T>> {
  Futures1(Object parent) {
    super(parent);
  }

  Futures1(Future<T> future) {
    super(future);
    future.setHandler(this);
  }

  @Override
  protected Futures1<T> create() {
    return new Futures1<>((Object)this);
  }
}
