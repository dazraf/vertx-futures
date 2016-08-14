package io.dazraf.vertx.futures.test.peek_tests;

import io.vertx.core.Future;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static io.dazraf.vertx.futures.FutureChain.*;
import static io.dazraf.vertx.futures.test.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class Peek3Tests {
  @Test
  public void thatASuccessfulFutureCanBePeeked() {
    Future<String> aFuture = Future.future(); // defer completion
    AtomicInteger countdown = new AtomicInteger(4);

    when(aFuture)
      .peekSuccess(s -> {
        assertThat(s, is(RESULT_MSG));
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
    aFuture.complete(RESULT_MSG);
    assertThat(countdown.get(), is(0));
  }

  @Test
  public void thatAFailedFutureCanBePeeked() {
    Future<String> aFuture = Future.future(); // defer completion
    AtomicInteger countdown = new AtomicInteger(4);

    when(aFuture)
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
    aFuture.fail(FAIL_MSG);
    assertThat(countdown.get(), is(0));
  }
}
