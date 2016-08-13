package io.dazraf.vertx.futures.test.tuple_tests;

import io.dazraf.vertx.futures.tuple.Tuple2;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static io.dazraf.vertx.futures.test.TestUtils.*;
import static io.dazraf.vertx.futures.tuple.Tuple.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

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
