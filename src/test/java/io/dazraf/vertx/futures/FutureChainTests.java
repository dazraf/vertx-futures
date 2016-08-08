package io.dazraf.vertx.futures;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import org.junit.Test;
import org.slf4j.Logger;

import static io.dazraf.vertx.futures.FutureChain.*;
import static io.vertx.core.Future.succeededFuture;
import static org.slf4j.LoggerFactory.*;

public class FutureChainTests {
  private static final Logger LOG = getLogger(FutureChainTests.class);

  @Test
  public void twoResultsTest() {
    when(getName(), getAge())
      .onSuccess((name, age) -> LOG.info("succeeded in getting name '{}' and age '{}", name, age))
      .then(this::composeMessage)
      .onSuccess(LOG::info);

  }

  @Test
  public void reuseEarlierResult() {
    when(getName())
      .then(name -> CompositeFuture.all(succeededFuture(name), getAge()), String.class, Integer.class)
      .then(this::composeMessage)
      .onSuccess(LOG::info);
  }

  private Future<String> composeMessage(String name, Integer age) {
    return succeededFuture("hello " + name + ", you are " + age + " year" + (age > 1 ? "s" : "") + " old");
  }

  Future<String> getName() {
    // could be async
    return succeededFuture("Fuzz");
  }

  Future<Integer> getAge() {
    return succeededFuture(45);
  }
}
