package io.dazraf.vertx.futures;

import io.dazraf.vertx.futures.tuple.Tuple;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import org.slf4j.Logger;

import java.util.List;

import static org.slf4j.LoggerFactory.*;

abstract class FutureChainN<T extends Tuple<T>, Derived extends FutureChainN<T, Derived>>
  extends FutureChainImpl<T, Derived> {

  private static final Logger LOG = getLogger(FutureChainN.class);

  protected FutureChainN() {

  }

  protected FutureChainN(List<Future> futures) {
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
