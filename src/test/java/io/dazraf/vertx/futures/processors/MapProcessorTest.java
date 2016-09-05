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
import static io.dazraf.vertx.futures.processors.MapProcessor.map;
import static io.dazraf.vertx.futures.processors.MapProcessor.mapOnResponse;
import static io.dazraf.vertx.futures.processors.RunProcessor.*;
import static io.vertx.core.Future.*;

@RunWith(VertxUnitRunner.class)
public class MapProcessorTest {

  @ClassRule
  public static final RunTestOnContext vertxContext = new RunTestOnContext();
  private static final String MSG = "message!";

  @Test
  public void mapValueTest(TestContext context) {
    Async async = context.async();
    when(futureMessage())
        .then(run(val -> context.assertEquals(MSG, val)))
        .then(map(val -> val + val))
        .then(run(val -> context.assertEquals(MSG + MSG, val)))
        .then(run(async::complete))
        .then(ifFailedRun(context::fail));
  }

  @Test
  public void map2ValuesTest(TestContext context) {
    Async async = context.async();
    when(futureMessage(), futureMessage())
        .then(run((val1, val2) -> context.assertEquals(MSG, val1).assertEquals(MSG, val2)))
        .then(map((val1, val2) -> val1 + val2))
        .then(run(val -> context.assertEquals(MSG + MSG, val)))
        .then(run(async::complete))
        .then(ifFailedRun(context::fail));
  }

  @Test
  public void map3ValuesTest(TestContext context) {
    Async async = context.async();
    when(futureMessage(), futureMessage(), futureMessage())
        .then(
            run((val1, val2, val3) -> context.assertEquals(MSG, val1).assertEquals(MSG, val2).assertEquals(MSG, val3)))
        .then(map((val1, val2, val3) -> val1 + val2 + val3))
        .then(run(val -> context.assertEquals(MSG + MSG + MSG, val)))
        .then(run(async::complete))
        .then(ifFailedRun(context::fail));
  }

  @Test
  public void map4ValuesTest(TestContext context) {
    Async async = context.async();
    when(futureMessage(), futureMessage(), futureMessage(), futureMessage())
        .then(run((val1, val2, val3, val4) ->
                      context
                          .assertEquals(MSG, val1).assertEquals(MSG, val2)
                          .assertEquals(MSG, val3)
                          .assertEquals(MSG, val4)))
        .then(map((val1, val2, val3, val4) -> val1 + val2 + val3 + val4))
        .then(run(val -> context.assertEquals(MSG + MSG + MSG + MSG, val)))
        .then(run(async::complete))
        .then(ifFailedRun(context::fail));
  }

  @Test
  public void mapResponseTest(TestContext context) {
    Async async = context.async();
    when(futureMessage(), futureMessage())
        .then(mapOnResponse(asyncResult -> {
          context.assertEquals(true, asyncResult.succeeded())
              .assertEquals(MSG, asyncResult.result().getT1())
              .assertEquals(MSG, asyncResult.result().getT2());
          return asyncResult.result().getT1() + asyncResult.result().getT2();
        }))
        .then(run(result -> context.assertEquals(MSG + MSG, result)))
        .then(run(async::complete))
        .then(ifFailedRun(context::fail));
  }

  @Test
  public void mapShouldNotExecuteIfChainFailed(TestContext context) {
    Async async = context.async();
    when(failedFuture(MSG))
      .then(map(s -> {
        context.fail("should not get here");
        throw new RuntimeException("failed");
      }))
      .then(run((Runnable) context::fail))
      .then(ifFailedRun(err -> async.complete()));
  }

  @Test
  public void exceptionFromMapIsCorrectlyHandled(TestContext context) {
    Async async = context.async();

    when(succeededFuture(MSG))
      .then(map(s -> {
        throw new RuntimeException("Error");
      }))
      .then(run(s -> context.fail("failed")))
      .then(ifFailedRun(err -> async.complete()));
  }

  private Future<String> futureMessage() {
    return succeededFuture(MSG);
  }
}
