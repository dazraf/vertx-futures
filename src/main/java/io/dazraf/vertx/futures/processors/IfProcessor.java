package io.dazraf.vertx.futures.processors;

import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.succeededFuture;

/**
 *
 */
public interface IfProcessor<T, R> extends FutureProcessor<T, R> {

  static <T, R> IfProcessor<T, R> ifSucceeded(FutureProcessor<T, R> processor) {
    return future -> {
      if (future.succeeded()) {
        return processor.apply(future);
      } else {
        return failedFuture(future.cause());
      }
    };
  }

  static <T> IfProcessor<T, T> ifFailed(FutureProcessor<T, T> processor) {
    return future -> {
      if (future.failed()) {
        return processor.apply(future);
      } else {
        return succeededFuture(future.result());
      }
    };
  }
}
