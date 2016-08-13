package io.dazraf.vertx.futures.test.map_tests;

import io.dazraf.vertx.futures.FutureChain;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static io.dazraf.vertx.futures.test.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class MapVoidTests {
  @Test
  public void thatSuccessfullFuture1CanBeMappedToVoid() {
    AtomicInteger counter = new AtomicInteger();

    FutureChain.when(aSucceededFuture())
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

    FutureChain.when(aFailedFuture())
      .onFail(counter::incrementAndGet)
      .onSuccess(() -> fail("should be a failed future"))
      .mapVoid()
      .onFail(counter::incrementAndGet)
      .onSuccess(() -> fail("should be a failed futurefail"));

    assertThat(counter.get(), is(2));
  }
}
