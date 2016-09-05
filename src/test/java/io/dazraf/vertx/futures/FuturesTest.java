package io.dazraf.vertx.futures;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import io.vertx.core.Future;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import static io.dazraf.vertx.futures.Futures.when;
import static io.dazraf.vertx.futures.processors.CallProcessor.call;
import static io.dazraf.vertx.futures.processors.RunProcessor.ifFailedRun;
import static io.dazraf.vertx.futures.processors.RunProcessor.run;
import static io.vertx.core.Future.succeededFuture;
import static org.slf4j.LoggerFactory.getLogger;

@RunWith(VertxUnitRunner.class)
public class FuturesTest {

  private static final Logger LOG = getLogger(FuturesTest.class);

  public static final int ID = 1;
  @ClassRule
  public static RunTestOnContext context = new RunTestOnContext();

  @Test
  public void generalTest() {
    when(getId())
        .then(call(id -> when(getName(id), getAge(id))))
        .then(call((name, age) -> composeMessage(name, age)))
        .then(run(result -> LOG.info(result)))
        .then(ifFailedRun(cause -> LOG.error("error handler", cause)));
  }

  private Future<Integer> getId() {
    return succeededFuture(ID);
  }


  private Future<String> getName(int id) {
    return succeededFuture("name-" + id);
  }

  private Future<Integer> getAge(int id) {
    return succeededFuture(42);
  }

  private Future<String> composeMessage(String name, int age) {
    return succeededFuture("Hi " + name + " you are " + age + " years old");
  }
}
