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
import io.vertx.core.Handler;

import static io.dazraf.vertx.futures.filters.FailureFilter.failure;
import static io.dazraf.vertx.futures.filters.SuccessFilter.success;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * A {@link FutureProcessor} to examine the state of the chain.
 * Any exceptions thrown by the peek consumer function are ignored.
 */
public interface PeekProcessor<T> extends FutureProcessor<T, T> {
  Logger LOG = getLogger(PeekProcessor.class);

  /**
   * Observe the state of the chain. Any exceptions from the consumer are ignored and do not affect the state of the chain.
   * @param consumer
   * @param <T>
   * @return
   */
  static <T> PeekProcessor<T> peekResponse(Handler<AsyncResult<T>> consumer) {
    return future -> {
      try {
        consumer.handle(future);
      } catch (Throwable error) {
        LOG.trace("peek function failed", error);
      }
      return future;
    };
  }


  static <T> PeekProcessor<T> peekFailure(Consumer<Throwable> consumer) {
    return peekResponse(failure(consumer));
  }

  /**
   * Peek at a single-value result from a successful chain
   * @param consumer
   * @param <T>
   * @return
   */
  static <T> PeekProcessor<T> peek(Consumer<T> consumer) {
    return peekResponse(success(consumer));
  }

  /**
   * Peek at a successful chain emitting a Tuple2
   * @param consumer
   * @param <T1>
   * @param <T2>
   * @return
   */
  static <T1, T2> PeekProcessor<Tuple2<T1, T2>> peek(Consumer2<T1, T2> consumer) {
    return peekResponse(success(consumer));
  }

  /**
   * Peek at a successful chain emitting a Tuple3
   * @param consumer
   * @param <T1>
   * @param <T2>
   * @param <T3>
   * @return
   */
  static <T1, T2, T3> PeekProcessor<Tuple3<T1, T2, T3>> peek(Consumer3<T1, T2, T3> consumer) {
    return peekResponse(success(consumer));
  }

  /**
   * Peek at a successful chain emitting a Tuple2
   * @param consumer
   * @param <T1>
   * @param <T2>
   * @param <T3>
   * @param <T4>
   * @return
   */
  static <T1, T2, T3, T4> PeekProcessor<Tuple4<T1, T2, T3, T4>> peek(Consumer4<T1, T2, T3, T4> consumer) {
    return peekResponse(success(consumer));
  }
}
