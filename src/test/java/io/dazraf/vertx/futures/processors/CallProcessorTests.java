package io.dazraf.vertx.futures.processors;

import io.dazraf.vertx.futures.TestUtils;
import io.dazraf.vertx.futures.VertxMatcherAssert;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.hamcrest.CoreMatchers;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.dazraf.vertx.futures.Futures.*;
import static io.dazraf.vertx.futures.TestUtils.*;
import static io.dazraf.vertx.futures.VertxMatcherAssert.assertThat;
import static io.dazraf.vertx.futures.processors.CallProcessor.*;
import static io.dazraf.vertx.futures.processors.RunProcessor.*;
import static io.vertx.core.Future.*;

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

  @Test
  public void exceptionThrownWithinCallFunctionShouldPropagateChain(TestContext context) {
    Async async = context.async();

    when(succeededFuture(RESULT_MSG))
      .then(call(str -> {
        throw new RuntimeException("failed");
      }))
      .then(run(() -> context.fail("this should never be executed")))
      .then(ifFailedRun(err -> async.complete()));
  }

  @Test
  public void canDestructure2(TestContext context) {
    Async async = context.async();
    when(succeededFuture(RESULT_MSG), succeededFuture(RESULT_INT))
      .then(call( (s, i) -> succeededFuture(s + i)))
      .then(run(str -> {
        assertThat(context, str, CoreMatchers.is(RESULT_MSG + RESULT_INT));
        async.complete();
      }))
      .then(ifFailedRun(context::fail));
  }

  @Test
  public void canDestructure3(TestContext context) {
    Async async = context.async();
    when(succeededFuture(RESULT_MSG), succeededFuture(RESULT_INT), succeededFuture(RESULT_BOOL))
      .then(call( (s, i, b) -> succeededFuture(s + i + b)))
      .then(run(str -> {
        assertThat(context, str, CoreMatchers.is(RESULT_MSG + RESULT_INT + RESULT_BOOL));
        async.complete();
      }))
      .then(ifFailedRun(context::fail));
  }

  @Test
  public void canDestructure4(TestContext context) {
    Async async = context.async();
    when(succeededFuture(RESULT_MSG), succeededFuture(RESULT_INT),
      succeededFuture(RESULT_BOOL), succeededFuture(RESULT_MSG))
      .then(call( (s, i, b, s2) -> succeededFuture(s + i + b + s2)))
      .then(run(str -> {
        assertThat(context, str, CoreMatchers.is(RESULT_MSG + RESULT_INT + RESULT_BOOL + RESULT_MSG));
        async.complete();
      }))
      .then(ifFailedRun(context::fail));
  }
}
