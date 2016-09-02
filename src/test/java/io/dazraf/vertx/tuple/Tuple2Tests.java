package io.dazraf.vertx.tuple;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static io.dazraf.vertx.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class Tuple2Tests {
  @Test
  public void createAndRetrieveTest() {
    final Tuple2<String, Integer> t = Tuple.tuple(RESULT_MSG, RESULT_INT);
    assertThat(t.getT1(), is(RESULT_MSG));
    assertThat(t.getT2(), is(RESULT_INT));
  }

  @Test
  public void createAndDestructure() {
    AtomicInteger countdown = new AtomicInteger(1);
    final Tuple2<String, Integer> t = Tuple.tuple(RESULT_MSG, RESULT_INT);
    assertThat(t.apply((s, i) -> s + i),  is(RESULT_MSG + RESULT_INT));
    t.accept((s, i) -> {
      assertThat(s, is(RESULT_MSG));
      assertThat(i, is(RESULT_INT));
      countdown.decrementAndGet();
    });
    assertThat(countdown.get(), is(0));
  }

  @Test
  public void positiveEquality() {
    assertThat(Tuple.tuple(RESULT_MSG, RESULT_INT), is(Tuple.tuple(RESULT_MSG, RESULT_INT)));
  }


  @Test
  public void negativeEquality() {
    assertThat(Tuple.tuple(RESULT_INT, RESULT_MSG), not(Tuple.tuple(RESULT_MSG, RESULT_INT)));
    assertThat(Tuple.tuple(RESULT_INT, RESULT_MSG), not(Tuple.tuple(RESULT_MSG, RESULT_INT, RESULT_BOOL)));
  }

  @Test
  public void hashTest() {
    Set<Tuple2<String, Integer>> set = new HashSet<>();
    Tuple2<String, Integer> tuple = Tuple.tuple(RESULT_MSG, RESULT_INT);
    set.add(tuple);
    assertTrue(set.contains(tuple));
  }
}
