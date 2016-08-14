package io.dazraf.vertx.futures.test.then_tests;

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
  private static final String NAME = "Jim";
  private static final int AGE = 40;
  private static final String ADDRESS = "Norwich";
  public static final int ID = 5;

  @Test
  public void oneResultTest() {
    when(getId())
    .then(this::getName)
      .peekSuccess(t -> assertThat(t, is(NAME)))
      .then(name -> composeMessage(name, 42))
      .onSuccess((Consumer<String>) LOG::info);
  }

  @Test
  public void oneThenOneTest() {
    when(getId())
      .then(this::getName)
      .peekSuccess(v -> assertThat(v, is(NAME)))
      .then(name -> composeMessage(name, AGE))
      .onSuccess(msg -> assertThat(msg, is(composeMessage(NAME, AGE))))
      .onSuccess((Consumer<String>) LOG::info);
  }

  @Test
  public void oneThenTwoTest() {
    when(getId())
      .then2(id -> all(getName(id), getAge(id)))
      .peekSuccess(tuple  -> assertThat(tuple, is(all(getName(ID), getAge(ID)))))
      .then(this::composeMessage)
      .onSuccess((Consumer<String>) LOG::info);
  }

  @Test
  public void twoThenOneTest() {
    when(getId())
      .then2(id -> all(getName(id), getAge(id)))
      .peekSuccess((name, age) -> LOG.info("peekSuccess: succeeded in getting name '{}' and age '{}", name, age))
      .onSuccess((name, age) -> LOG.info("onSuccess: succeeded in getting name '{}' and age '{}", name, age))
      .then((name, age) -> composeMessage(name, age))
      .onSuccess(result -> LOG.info(result))
      .onFail(cause -> LOG.error("error handler", cause));
  }

  @Test
  public void threeThenOneTest() {
    String result =
      when(getId())
      .then3(id -> all(getName(id), getAge(id), getAddress(id)))
      .peekSuccess((name, age, address) -> LOG.info("peekSuccess: succeeded in getting name '{}' age '{} address {}", name, age, address))
      .onSuccess((name, age, address) -> LOG.info("onSuccess: succeeded in getting name '{}' age '{} address {}", name, age, address))
      .then((name, age, address) -> succeededFuture(name + age + address))
      .result();

    assertThat(result, is (NAME + AGE + ADDRESS));
  }

  private Future<String> composeMessage(String name, Integer age) {
    return succeededFuture("hello " + name + ", you are " + age + " year" + (age > 1 ? "s" : "") + " old");
  }

  private Future<Integer> getId() { return succeededFuture(ID); }
  private Future<String> getName(int id) {
    return succeededFuture(NAME);
  }

  private Future<Integer> getAge(int id) {
    return succeededFuture(AGE);
  }

  private Future<String> getAddress(int id) { return succeededFuture(ADDRESS); }

  private void call(Integer i, String s) {
    LOG.info("{} {}", i, s);
  }
}
