package io.dazraf.vertx.futures;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class MapVoidTests {
  @Test
  public void thatSuccessfullFuture1CanBeMappedToVoid() {
    AtomicInteger counter = new AtomicInteger();

    FutureChain.when(TestUtils.aSucceededFuture())
      .onSuccess(counter::incrementAndGet)
      .onFail(() -> fail("should not fail"))
      .mapVoid()
      .onSuccess(counter::incrementAndGet)
      .onFail(() -> fail("should not fail"));

    assertThat(counter.get(), is(2));
  }

  @Test
  public void thatFailedFuture1CanBeMappedToVoid() {
    AtomicInteger counter = new AtomicInteger();

    FutureChain.when(TestUtils.aFailedFuture())
      .onFail(counter::incrementAndGet)
      .onSuccess(() -> fail("should be a failed future"))
      .mapVoid()
      .onFail(counter::incrementAndGet)
      .onSuccess(() -> fail("should be a failed futurefail"));

    assertThat(counter.get(), is(2));
  }
}
