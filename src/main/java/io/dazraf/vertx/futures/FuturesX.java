package io.dazraf.vertx.futures;

import io.dazraf.vertx.futures.tuple.Tuple;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

import java.util.List;

public abstract class FuturesX<T extends Tuple<T>, Derived extends FuturesX<T, Derived>>
  extends FuturesImpl<T, Derived> {

  FuturesX(Object parent) {
    super(parent);
  }

  FuturesX(List<Future> futures) {
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
