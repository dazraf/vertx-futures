package io.dazraf.vertx.tuple;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static io.dazraf.vertx.TestUtils.*;
import static io.dazraf.vertx.tuple.Tuple.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class Tuple5Test {
  @Test
  public void createAndRetrieveTest() {
    final Tuple5<String, Integer, Boolean, Integer, String> t =
      tuple(RESULT_MSG, RESULT_INT,
      RESULT_BOOL, RESULT_INT,
      RESULT_MSG);
    assertThat(t.getT1(), is(RESULT_MSG));
    assertThat(t.getT2(), is(RESULT_INT));
    assertThat(t.getT3(), is(RESULT_BOOL));
    assertThat(t.getT4(), is(RESULT_INT));
    assertThat(t.getT5(), is(RESULT_MSG));
  }

  @Test
  public void createAndDestructure() {
    AtomicInteger countdown = new AtomicInteger(1);
    final Tuple5<String, Integer, Boolean, Integer, String> t = tuple(RESULT_MSG, RESULT_INT, RESULT_BOOL, RESULT_INT, RESULT_MSG);
    assertThat(t.apply((s, i, b, i2, s2) -> s + i + b + i2 + s2),  is(RESULT_MSG + RESULT_INT + RESULT_BOOL + RESULT_INT + RESULT_MSG));
    t.accept((s, i, b, i2, s2) -> {
      assertThat(s, is(RESULT_MSG));
      assertThat(i, is(RESULT_INT));
      assertThat(b, is(RESULT_BOOL));
      assertThat(i2, is(RESULT_INT));
      assertThat(s2, is(RESULT_MSG));
      countdown.decrementAndGet();
    });
    assertThat(countdown.get(), is(0));
  }

  @Test
  public void positiveEquality() {
    assertThat(tuple(RESULT_MSG, RESULT_INT, RESULT_BOOL, RESULT_MSG, RESULT_INT), is(
        tuple(RESULT_MSG, RESULT_INT, RESULT_BOOL, RESULT_MSG, RESULT_INT)));
  }


  @Test
  public void negativeEquality() {
    assertThat(tuple(RESULT_INT, RESULT_MSG, RESULT_BOOL, RESULT_MSG, RESULT_BOOL), not(
        tuple(RESULT_MSG, RESULT_INT, RESULT_BOOL, RESULT_MSG, RESULT_BOOL)));
    assertThat(tuple(RESULT_INT, RESULT_MSG, RESULT_BOOL, RESULT_MSG, RESULT_BOOL), not(
        tuple(RESULT_MSG, RESULT_INT, RESULT_BOOL, RESULT_BOOL, RESULT_BOOL)));
  }

  @Test
  public void hashTest() {
    Set<Tuple5<String, Integer, Boolean, String, Boolean>> set = new HashSet<>();
    Tuple5<String, Integer, Boolean, String, Boolean> tuple = tuple(RESULT_MSG, RESULT_INT, RESULT_BOOL, RESULT_MSG, RESULT_BOOL);
    set.add(tuple);
    assertTrue(set.contains(tuple));
  }
}
