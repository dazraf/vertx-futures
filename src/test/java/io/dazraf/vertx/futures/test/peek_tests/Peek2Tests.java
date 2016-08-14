package io.dazraf.vertx.futures.test.peek_tests;

import io.vertx.core.Future;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static io.dazraf.vertx.futures.FutureChain.*;
import static io.dazraf.vertx.futures.test.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class Peek2Tests {
  @Test
  public void thatASuccessfulFutureCanBePeeked() {
    Future<String> stringFuture = Future.future(); // defer completion
    Future<Integer> intFuture = Future.future();
    Future<Boolean> booleanFuture = Future.future();

    AtomicInteger countdown = new AtomicInteger(4);

    when(stringFuture, intFuture, booleanFuture)
      .peekSuccess((s, i, b) -> {
        assertThat(s, is(RESULT_MSG));
        assertThat(i, is(RESULT_INT));
        assertThat(b, is(RESULT_BOOL));
        countdown.decrementAndGet();
      })
      .peekFail(() -> Assert.fail("should never call here, because future is successful"))
      .peekSuccess(countdown::decrementAndGet)
      .peekComplete(ar -> {
        assertThat(ar.succeeded(), is(true));
        countdown.decrementAndGet();
      })
      .peekComplete(countdown::decrementAndGet);

    // now complete the future successfully
    stringFuture.complete(RESULT_MSG);
    intFuture.complete(RESULT_INT);
    booleanFuture.complete(RESULT_BOOL);
    assertThat(countdown.get(), is(0));
  }

  @Test
  public void thatAFailedFutureCanBePeeked() {
    Future<String> stringFuture = Future.future(); // defer completion
    Future<Integer> intFuture = Future.future();
    Future<Boolean> booleanFuture = Future.future();

    AtomicInteger countdown = new AtomicInteger(4);

    when(stringFuture, intFuture, booleanFuture)
      .peekFail(e -> {
        assertThat(e.getMessage(), is(FAIL_MSG));
        countdown.decrementAndGet();
      })
      .peekSuccess(() -> Assert.fail("should never call here, because future is failed"))
      .peekFail(countdown::decrementAndGet)
      .peekComplete(ar -> {
        assertThat(ar.failed(), is(true));
        countdown.decrementAndGet();
      })
      .peekComplete(countdown::decrementAndGet);

    // now complete the future successfully
    stringFuture.complete(RESULT_MSG);
    intFuture.complete(RESULT_INT);
    booleanFuture.fail(FAIL_MSG);
    assertThat(countdown.get(), is(0));
  }
}
