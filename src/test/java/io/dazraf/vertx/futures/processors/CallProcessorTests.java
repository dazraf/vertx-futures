package io.dazraf.vertx.futures.processors;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.dazraf.vertx.futures.Futures.*;
import static io.dazraf.vertx.futures.processors.CallProcessor.*;
import static io.dazraf.vertx.futures.processors.RunProcessor.*;
import static io.vertx.core.Future.failedFuture;

@RunWith(VertxUnitRunner.class)
public class CallProcessorTests {
  private static final String MSG = "message";
  @ClassRule
  public static RunTestOnContext rule = new RunTestOnContext();

  @Test
  public void callShouldNotExecuteIfChainFailed(TestContext context) {
    Async async = context.async();
    when(failedFuture(MSG))
      .then(call(s -> {
        context.fail("should not get here");
        throw new RuntimeException("failed");
      }))
      .then(run((Runnable) context::fail))
      .then(ifFailedRun(err -> async.complete()));
  }
}
