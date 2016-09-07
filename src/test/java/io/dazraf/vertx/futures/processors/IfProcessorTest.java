package io.dazraf.vertx.futures.processors;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.dazraf.vertx.TestUtils.*;
import static io.dazraf.vertx.futures.Futures.when;
import static io.dazraf.vertx.futures.VertxMatcherAssert.assertThat;
import static io.dazraf.vertx.futures.processors.IfProcessor.*;
import static io.dazraf.vertx.futures.processors.RunProcessor.*;
import static io.vertx.core.Future.*;
import static org.hamcrest.CoreMatchers.is;

@RunWith(VertxUnitRunner.class)
public class IfProcessorTest {
  @ClassRule
  public static final RunTestOnContext vertxContext = new RunTestOnContext();

  @Test
  public void test_givenSuccess_canExecuteHappyPath(TestContext context) {
    Async async = context.async();
    when(succeededFuture(RESULT_MSG))
      .then(ifSucceeded(asyncResult -> succeededFuture(RESULT_BOOL)))
      .then(run(result -> assertThat(context, result, is(RESULT_BOOL))))
      .then(run(async::complete))
      .then(RunProcessor.runOnFail(context::fail));
  }

  @Test
  public void test_givenSuccess_ifFailedDoesNotExecute(TestContext context) {
    Async async = context.async();
    when(succeededFuture(RESULT_MSG))
      .then(ifFailed(asyncResult -> succeededFuture(RESULT_MSG + RESULT_MSG)))
      .then(run(result -> assertThat(context, result, is(RESULT_MSG))))
      .then(run(async::complete))
      .then(RunProcessor.runOnFail(context::fail));
  }


  @Test
  public void test_givenFailure_canExecuteFailureHandler(TestContext context) {
    Async async = context.async();
    when(failedFuture(RESULT_MSG))
      .then(ifFailed(asyncResult -> succeededFuture(RESULT_BOOL)))
      .then(run(result -> assertThat(context, result, is(RESULT_BOOL))))
      .then(run(async::complete))
      .then(RunProcessor.runOnFail(context::fail));
  }


  @Test
  public void test_givenFailure_ifSucceededDoesNotExecute(TestContext context) {
    Async async = context.async();
    when(failedFuture(RESULT_MSG))
      .then(ifSucceeded(asyncResult -> failedFuture(RESULT_MSG + RESULT_MSG)))
      .then(runOnResponse(ar -> {
        assertThat(context, ar.failed(), is(true));
        assertThat(context, ar.cause().getMessage(), is(RESULT_MSG));
      }))
      .then(RunProcessor.runOnFail(err -> async.complete()))
      .then(run((Runnable) context::fail));
  }
}
