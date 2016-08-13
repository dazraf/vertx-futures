package io.dazraf.vertx.futures.test.then_tests;

import io.dazraf.vertx.futures.tuple.Tuple2;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import org.junit.Test;
import org.slf4j.Logger;

import java.util.function.Consumer;

import static io.dazraf.vertx.futures.FutureChain.*;
import static io.dazraf.vertx.futures.tuple.Tuple.*;
import static io.vertx.core.Future.succeededFuture;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.slf4j.LoggerFactory.*;

public class ThenTests {
  private static final Logger LOG = getLogger(ThenTests.class);
  private static final String NAME = "Fuzz";
  private static final int AGE = 45;

  @Test
  public void oneResultTest() {
    when(getName())
      .peekSuccess(t -> assertThat(t, is(NAME)))
      .then(name -> composeMessage(name, 42))
      .onSuccess((Consumer<String>) LOG::info);
  }

  @Test
  public void oneThenOneTest() {
    when(getName())
      .peekSuccess(v -> assertThat(v, is(NAME)))
      .then(name -> composeMessage(name, AGE))
      .onSuccess(msg -> assertThat(msg, is(composeMessage(NAME, AGE))))
      .onSuccess((Consumer<String>) LOG::info);
  }

  @Test
  public void oneThenTwoTest() {
    when(getName())
      .peekSuccess(v -> assertThat(v, is(NAME)))
      .then2(name -> all(getName(), getAge()))
      .peekSuccess(tuple  -> assertThat(tuple, is(all(getName(), getAge()))))
      .then(this::composeMessage)
      .onSuccess((Consumer<String>) LOG::info);
  }

  @Test
  public void twoThenOneTest() {
    when(getName(), getAge())
      .peekSuccess((name, age) -> LOG.info("peekSuccess: succeeded in getting name '{}' and age '{}", name, age))
      .onSuccess((name, age) -> LOG.info("onSuccess: succeeded in getting name '{}' and age '{}", name, age))
      .then(this::composeMessage)
      .onSuccess((Consumer<String>) LOG::info);
  }

  private Future<String> composeMessage(String name, Integer age) {
    return succeededFuture("hello " + name + ", you are " + age + " year" + (age > 1 ? "s" : "") + " old");
  }

  private Future<String> getName() {
    // could be async
    return succeededFuture(NAME);
  }

  Future<Integer> getAge() {
    return succeededFuture(AGE);
  }

  private void foo() {
    this.<Integer, String> struct2(bar())
      .accept(this::call);
  }

  private CompositeFuture bar() {
    return CompositeFuture.all(succeededFuture(1), succeededFuture("hello"));
  }

  private <T1, T2> Tuple2<T1, T2> struct2(CompositeFuture future) {
    return new Tuple2<>(future);
  }

  private void call(Integer i, String s) {
    LOG.info("{} {}", i, s);
  }
}