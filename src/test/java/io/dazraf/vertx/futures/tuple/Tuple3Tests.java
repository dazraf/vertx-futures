package io.dazraf.vertx.futures.tuple;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static io.dazraf.vertx.futures.TestUtils.RESULT_BOOL;
import static io.dazraf.vertx.futures.TestUtils.RESULT_INT;
import static io.dazraf.vertx.futures.TestUtils.RESULT_MSG;
import static io.dazraf.vertx.futures.tuple.Tuple.all;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class Tuple3Tests {
  @Test
  public void createAndRetrieveTest() {
    final Tuple3<String, Integer, Boolean> t = all(RESULT_MSG, RESULT_INT, RESULT_BOOL);
    assertThat(t.getT1(), is(RESULT_MSG));
    assertThat(t.getT2(), is(RESULT_INT));
    assertThat(t.getT3(), is(RESULT_BOOL));
  }

  @Test
  public void createAndDestructure() {
    AtomicInteger countdown = new AtomicInteger(1);
    final Tuple3<String, Integer, Boolean> t = all(RESULT_MSG, RESULT_INT, RESULT_BOOL);
    assertThat(t.apply((s, i, b) -> s + i + b),  is(RESULT_MSG + RESULT_INT + RESULT_BOOL));
    t.accept((s, i, b) -> {
      assertThat(s, is(RESULT_MSG));
      assertThat(i, is(RESULT_INT));
      assertThat(b, is(RESULT_BOOL));
      countdown.decrementAndGet();
    });
    assertThat(countdown.get(), is(0));
  }

  @Test
  public void positiveEquality() {
    assertThat(all(RESULT_MSG, RESULT_INT, RESULT_BOOL), is(all(RESULT_MSG, RESULT_INT, RESULT_BOOL)));
  }


  @Test
  public void negativeEquality() {
    assertThat(all(RESULT_INT, RESULT_MSG, RESULT_BOOL), not(all(RESULT_MSG, RESULT_INT, RESULT_BOOL)));
    assertThat(all(RESULT_INT, RESULT_MSG, RESULT_BOOL), not(all(RESULT_MSG, RESULT_INT, RESULT_BOOL, RESULT_BOOL)));
  }

  @Test
  public void hashTest() {
    Set<Tuple3<String, Integer, Boolean>> set = new HashSet<>();
    Tuple3<String, Integer, Boolean> tuple = all(RESULT_MSG, RESULT_INT, RESULT_BOOL);
    set.add(tuple);
    assertTrue(set.contains(tuple));
  }
}
