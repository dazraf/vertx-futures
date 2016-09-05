package io.dazraf.vertx.futures.processors;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.vertx.core.Future;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import static io.dazraf.vertx.futures.Futures.when;
import static io.dazraf.vertx.futures.processors.CallProcessor.call;
import static io.dazraf.vertx.futures.processors.MapProcessor.map;
import static io.dazraf.vertx.futures.processors.RunProcessor.ifFailedRun;
import static io.dazraf.vertx.futures.processors.RunProcessor.run;
import static io.vertx.core.Future.failedFuture;
import static io.vertx.core.Future.future;
import static io.vertx.core.Future.succeededFuture;

@RunWith(VertxUnitRunner.class)
public class RunProcessorTest {
  @ClassRule
  public static final RunTestOnContext vertxContext = new RunTestOnContext();
  private static final int NUMBER = 1;
  private static final String MSG = "one";

  @Test
  public void testSimpleSuccess(TestContext context) {
    Async async = context.async();
    when(futureMessage())
        .then(run(result -> {
          context.assertEquals(MSG, result);
          async.complete();
        }))
        .then(ifFailedRun(context::fail));
  }

  @Test
  public void testSimpleAsyncSuccess(TestContext context) {
    Async async = context.async();
    Future<String> future = future();
    when(future)
        .then(run(msg -> context.assertEquals(MSG, msg)))
        .then(run(async::complete))
        .then(ifFailedRun(context::fail));
    vertxContext.vertx().setTimer(10, id -> future.complete(MSG));
  }

  @Test
  public void testSimpleFailure(TestContext context) {
    Async async = context.async();
    when(failedFuture(MSG))
        .then(run(() -> context.fail("should have failed")))
        .then(ifFailedRun(err -> context.assertEquals(MSG, err.getMessage())))
        .then(ifFailedRun(err -> async.complete()));
  }

  @Test
  public void testSimpleAsyncFailure(TestContext context) {
    Async async = context.async();
    String MSG = "error";
    Future<String> future = future();
    when(future)
        .then(run(() -> context.fail("should have failed")))
        .then(ifFailedRun((err -> context.assertEquals(MSG, err.getMessage()))))
        .then(ifFailedRun(err -> async.complete()));
    vertxContext.vertx().setTimer(10, id -> future.fail(MSG));
  }

  @Test
  public void test2SuccessfulFlow(TestContext context) {
    Async async = context.async();
    when(futureMessage(), futureNumber())
        .then(map((msg, number) -> msg + number))
        .then(run(val -> context.assertEquals(MSG + NUMBER, val)))
        .then(run(async::complete))
        .then(ifFailedRun(context::fail));
  }

  @Test
  public void test2Then1SuccessfulFlow(TestContext context) {
    Async async = context.async();
    when(futureMessage(), futureNumber())
        .then(call((msg, number) -> succeededFuture(msg + number)))
        .then(run(val -> context.assertEquals(MSG + NUMBER, val)))
        .then(run(async::complete))
        .then(ifFailedRun(context::fail));
  }

  @Test
  public void test3Then2Then1SuccessfulFlow(TestContext context) {
    Async async = context.async();
    when(futureMessage(), futureNumber(), futureMessage())
        .then(call((msg1, number, msg2) -> when(succeededFuture(msg1 + msg2), succeededFuture(number))))
        .then(call((msg, number) -> succeededFuture(msg + number)))
        .then(run(val -> context.assertEquals(MSG + MSG + NUMBER, val)))
        .then(run(async::complete))
        .then(ifFailedRun(context::fail));
  }

  @Test
  public void thatExceptionInRunFailsChain(TestContext context) {
    Async async = context.async();
    when(futureMessage())
        .then(run(() -> { throw new RuntimeException("error"); } ))
        .then(ifFailedRun(err -> async.complete()))
        .then(run(() -> context.fail("failed")));
  }

  private Future<Integer> futureNumber() {
    return succeededFuture(NUMBER);
  }

  private Future<String> futureMessage() {
    return succeededFuture(MSG);
  }
}
