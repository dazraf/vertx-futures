package io.dazraf.vertx.futures;

import io.dazraf.vertx.futures.tuple.Tuple;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

import java.util.List;

public abstract class FutureChainX<T extends Tuple<T>, Derived extends FutureChainX<T, Derived>>
  extends FutureChainImpl<T, Derived> {

  protected FutureChainX() {

  }

  protected FutureChainX(List<Future> futures) {
    CompositeFuture.all(futures).setHandler(ar -> {
      if (ar.succeeded()) {
        complete(createTuple(ar.result()));
      } else {
        fail(ar.cause());
      }
    });
  }

  protected abstract T createTuple(CompositeFuture resolvedFuture);

}
