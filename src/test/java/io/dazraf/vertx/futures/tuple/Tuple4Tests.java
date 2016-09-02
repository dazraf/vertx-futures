package io.dazraf.vertx.futures.tuple;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import io.dazraf.vertx.tuple.Tuple4;

import static io.dazraf.vertx.futures.TestUtils.*;
import static io.dazraf.vertx.tuple.Tuple.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class Tuple4Tests {
  @Test
  public void createAndRetrieveTest() {
    final Tuple4<String, Integer, Boolean, Integer> t = all(RESULT_MSG, RESULT_INT, RESULT_BOOL, RESULT_INT);
    assertThat(t.getT1(), is(RESULT_MSG));
    assertThat(t.getT2(), is(RESULT_INT));
    assertThat(t.getT3(), is(RESULT_BOOL));
    assertThat(t.getT4(), is(RESULT_INT));
  }

  @Test
  public void createAndDestructure() {
    AtomicInteger countdown = new AtomicInteger(1);
    final Tuple4<String, Integer, Boolean, Integer> t = all(RESULT_MSG, RESULT_INT, RESULT_BOOL, RESULT_INT);
    assertThat(t.apply((s, i, b, i2) -> s + i + b + i2),  is(RESULT_MSG + RESULT_INT + RESULT_BOOL + RESULT_INT));
    t.accept((s, i, b, i2) -> {
      assertThat(s, is(RESULT_MSG));
      assertThat(i, is(RESULT_INT));
      assertThat(b, is(RESULT_BOOL));
      assertThat(i2, is(RESULT_INT));
      countdown.decrementAndGet();
    });
    assertThat(countdown.get(), is(0));
  }

  @Test
  public void positiveEquality() {
    assertThat(all(RESULT_MSG, RESULT_INT, RESULT_BOOL, RESULT_MSG), is(all(RESULT_MSG, RESULT_INT, RESULT_BOOL, RESULT_MSG)));
  }


  @Test
  public void negativeEquality() {
    assertThat(all(RESULT_INT, RESULT_MSG, RESULT_BOOL, RESULT_MSG), not(all(RESULT_MSG, RESULT_INT, RESULT_BOOL, RESULT_MSG)));
    assertThat(all(RESULT_INT, RESULT_MSG, RESULT_BOOL, RESULT_MSG), not(all(RESULT_MSG, RESULT_INT, RESULT_BOOL, RESULT_BOOL)));
  }

  @Test
  public void hashTest() {
    Set<Tuple4<String, Integer, Boolean, String>> set = new HashSet<>();
    Tuple4<String, Integer, Boolean, String> tuple = all(RESULT_MSG, RESULT_INT, RESULT_BOOL, RESULT_MSG);
    set.add(tuple);
    assertTrue(set.contains(tuple));
  }
}
