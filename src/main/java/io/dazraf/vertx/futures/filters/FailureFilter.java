package io.dazraf.vertx.futures.filters;

import java.util.function.Consumer;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * Filters the state of a Future for failure, calling a handler function
 */
public interface FailureFilter<T> extends Handler<AsyncResult<T>> {
  static <T> Handler<AsyncResult<T>> failure(Consumer<Throwable> consumer) {
    return result -> {
      if (result.failed()) {
        consumer.accept(result.cause());
      }
    };
  }
}
