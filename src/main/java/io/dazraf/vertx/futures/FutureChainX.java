package io.dazraf.vertx.futures;

import io.dazraf.vertx.futures.tuple.Tuple;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

import java.util.List;

public abstract class FutureChainX<T extends Tuple<T>, Derived extends FutureChainX<T, Derived>>
  extends FutureChainImpl<T, Derived> {

  FutureChainX(Object parent) {
    super(parent);
  }

  FutureChainX(List<Future> futures) {
    super(CompositeFuture.all(futures));
    CompositeFuture p = (CompositeFuture)parent;
    p.setHandler(ar -> {
      if (ar.succeeded()) {
        super.complete(createTuple(ar.result()));
      } else {
        super.fail(ar.cause());
      }
    });
  }

  protected abstract T createTuple(CompositeFuture resolvedFuture);
}
