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
import static io.dazraf.vertx.futures.processors.RunProcessor.runOnFail;
import static io.dazraf.vertx.futures.processors.RunProcessor.run;
import static io.vertx.core.Future.succeededFuture;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.slf4j.LoggerFactory.getLogger;

@RunWith(VertxUnitRunner.class)
public class FuturesTest {

  private static final Logger LOG = getLogger(FuturesTest.class);

  private static final int ID = 1;
  private  static final String FAILURE_MSG = "Failure!";
  @ClassRule
  public static RunTestOnContext context = new RunTestOnContext();

  @Test
  public void test_1then2then1_happypath_succeeds() {
    when(getId())
        .then(call(id -> when(getName(id), getAge(id))))
        .then(call((name, age) -> composeMessage(name, age)))
        .then(run(result -> LOG.info(result)))
        .then(runOnFail(cause -> LOG.error("error handler", cause)));
  }

  @Test
  public void test_unresolvedFutureImpl_canBeFailedWithString() {
    Futures<Integer> future = new FuturesImpl<>(null);
    future.fail(FAILURE_MSG);
    assertTrue(future.failed());
    assertThat(future.cause().getMessage(), is(FAILURE_MSG));
  }

  @Test
  public void test_canComplete() {
    Futures<Integer> future = new FuturesImpl<>(null);
    assertFalse(future.isComplete());
    future.complete();
    assertTrue(future.isComplete() && future.result() == null);
  }

  @Test
  public void test_whenOfFutures_returnsSame() {
    Future<String> f1 = Futures.when(succeededFuture("hello"));
    Future<String> f2 = Futures.when(f1);
    assertSame(f1, f2);
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
