package io.dazraf.vertx.futures.processors;

import org.slf4j.Logger;

import java.util.function.Consumer;

import io.dazraf.vertx.consumer.Consumer2;
import io.dazraf.vertx.consumer.Consumer3;
import io.dazraf.vertx.consumer.Consumer4;
import io.dazraf.vertx.tuple.Tuple2;
import io.dazraf.vertx.tuple.Tuple3;
import io.dazraf.vertx.tuple.Tuple4;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.dazraf.vertx.futures.Futures;

import static io.dazraf.vertx.futures.filters.FailureFilter.failure;
import static io.dazraf.vertx.futures.filters.SuccessFilter.success;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * This interface is a {@link FutureProcessor} that executes a function
 * given a state change in a {@link Futures} chain.
 *
 * No result is collected from the called function.
 * The type of the {@link Futures} chain does not alter.
 * An exception raised by the called function will cause the {@link Futures} chain to fail with the respective cause.
 */
public interface RunProcessor<T> extends FutureProcessor<T, T> {

  Logger LOG = getLogger(RunProcessor.class);

  /**
   * Observe the state of the chain. Any exceptions from the consumer will cause the chain to fail.
   * @param consumer
   * @param <T>
   * @return
   */
  static <T> RunProcessor<T> runOnResponse(Handler<AsyncResult<T>> consumer) {
    return future -> {
      Future<T> result = Future.future();
      try {
        consumer.handle(future);
        result.completer().handle(future);
      } catch (Throwable error) {
        LOG.error("consumer function failed", error);
        result.fail(error);
      }
      return result;
    };
  }

  static <T> RunProcessor<T> run(Runnable runnable) {
    return runOnResponse(success(runnable));
  }

  static <T> RunProcessor<T> run(Consumer<T> consumer) {
    return runOnResponse(success(consumer));
  }

  static <T1, T2> RunProcessor<Tuple2<T1, T2>> run(Consumer2<T1, T2> consumer) {
    return runOnResponse(success(consumer));
  }

  static <T1, T2, T3> RunProcessor<Tuple3<T1, T2, T3>> run(Consumer3<T1, T2, T3> consumer) {
    return runOnResponse(success(consumer));
  }

  static <T1, T2, T3, T4> RunProcessor<Tuple4<T1, T2, T3, T4>> run(Consumer4<T1, T2, T3, T4> consumer) {
    return runOnResponse(success(consumer));
  }

  static <T> RunProcessor<T> ifFailedRun(Consumer<Throwable> consumer) {
    return runOnResponse(failure(consumer));
  }

}
