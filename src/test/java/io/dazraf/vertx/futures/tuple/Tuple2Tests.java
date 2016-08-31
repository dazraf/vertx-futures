package io.dazraf.vertx.futures.tuple;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static io.dazraf.vertx.futures.TestUtils.RESULT_INT;
import static io.dazraf.vertx.futures.TestUtils.RESULT_MSG;
import static io.dazraf.vertx.futures.tuple.Tuple.all;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class Tuple2Tests {
  @Test
  public void createAndRetrieveTest() {
    final Tuple2<String, Integer> t = all(RESULT_MSG, RESULT_INT);
    assertThat(t.getT1(), is(RESULT_MSG));
    assertThat(t.getT2(), is(RESULT_INT));
  }

  @Test
  public void createAndDestructure() {
    AtomicInteger countdown = new AtomicInteger(1);
    final Tuple2<String, Integer> t = all(RESULT_MSG, RESULT_INT);
    assertThat(t.apply((s, i) -> s + i),  is(RESULT_MSG + RESULT_INT));
    t.accept((s, i) -> {
      assertThat(s, is(RESULT_MSG));
      assertThat(i, is(RESULT_INT));
      countdown.decrementAndGet();
    });
    assertThat(countdown.get(), is(0));
  }
}
