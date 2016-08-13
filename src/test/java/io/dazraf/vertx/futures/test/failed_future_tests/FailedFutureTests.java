package io.dazraf.vertx.futures.test.failed_future_tests;

import io.dazraf.vertx.futures.test.TestUtils;
import io.vertx.core.Future;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static io.dazraf.vertx.futures.FutureChain.*;
import static io.dazraf.vertx.futures.test.TestUtils.*;
import static io.dazraf.vertx.futures.tuple.Tuple.*;
import static io.vertx.core.Future.future;
import static io.vertx.core.Future.succeededFuture;
import static io.vertx.core.Future.failedFuture;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class FailedFutureTests {

  @Test
  public void thatFailureCanBeInterceptNTimes() {
    AtomicInteger gate = new AtomicInteger();

    when(TestUtils.aFailedFuture())
      .onSuccess(() -> fail("should not succeed here"))
      .onFail(cause -> gate.incrementAndGet())
      .onFail(cause -> gate.incrementAndGet())
      .onComplete(ar -> assertThat(ar.failed(), is(true)))
      .onComplete(gate::incrementAndGet);

    assertThat(gate.get(), is(3));
  }

  @Test
  public void thatFailureCanBeConvertedToAnotherFuture() {
    Future<String> somethingThatFails = future();
    when(somethingThatFails)
      .onSuccess(() -> fail("should not succeed here"))
      .ifFailed(() -> succeededFuture(FAIL_MSG));
    somethingThatFails.fail(FAIL_MSG);
  }

  @Test
  public void thatFailureCanBeInterceptedFromTuple2Future() {
    when(all(TestUtils.aFailedFuture(), TestUtils.aSucceededFuture()))
      .onSuccess(() -> fail("should not be successful"))
      .onFail(t -> assertThat(t.getCause(), is(FAIL_MSG)));
  }

  @Test
  public void thatFailureCanBeInterceptedFromTuple3Future() {
    when(all(TestUtils.aFailedFuture(), TestUtils.aSucceededFuture(), TestUtils.aSucceededFuture()))
      .onSuccess(() -> fail("should not be successful"))
      .onFail(t -> assertThat(t.getCause(), is(FAIL_MSG)));
  }

  @Test
  public void thatFailureCanBeConvertedToTuple2Future() {
    when(TestUtils.aFailedFuture(), TestUtils.aSucceededFuture())
      .ifFailed2(t -> all(succeededFuture(RESULT_MSG), TestUtils.aSucceededFuture()))
      .onFail(t -> fail("should be successful now"))
      .onSuccess((r1, r2) -> {
        assertThat(r1, is(RESULT_MSG));
        assertThat(r2, is(RESULT_MSG));
      });
  }
  @Test
  public void thatFailureCanBeConvertedToTuple3Future() {
    when(TestUtils.aFailedFuture(), TestUtils.aSucceededFuture(), TestUtils.aSucceededFuture())
      .ifFailed3(t -> all(succeededFuture(RESULT_MSG), TestUtils.aSucceededFuture(), TestUtils.aSucceededFuture()))
      .onFail(t -> fail("should be successful now"))
      .onSuccess((r1, r2, r3) -> {
        assertThat(r1, is(RESULT_MSG));
        assertThat(r2, is(RESULT_MSG));
        assertThat(r3, is(RESULT_MSG));
      });
  }

}
