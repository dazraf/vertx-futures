package io.dazraf.vertx.futures.processors;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static io.dazraf.vertx.futures.Futures.when;
import static io.dazraf.vertx.TestUtils.*;
import static io.dazraf.vertx.futures.VertxMatcherAssert.*;
import static io.dazraf.vertx.futures.processors.CallProcessor.*;
import static io.dazraf.vertx.futures.processors.RunProcessor.*;
import static io.vertx.core.Future.*;
import static org.hamcrest.CoreMatchers.*;

@RunWith(VertxUnitRunner.class)
public class CallProcessorTests {
  private static final String MSG = "message";
  private static final String FAILED = "failed";
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
      .then(call((s, i) -> succeededFuture(s + i)))
      .then(run(str -> {
        assertThat(context, str, is(RESULT_MSG + RESULT_INT));
        async.complete();
      }))
      .then(ifFailedRun(context::fail));
  }

  @Test
  public void canDestructure3(TestContext context) {
    Async async = context.async();
    when(succeededFuture(RESULT_MSG), succeededFuture(RESULT_INT), succeededFuture(RESULT_BOOL))
      .then(call((s, i, b) -> succeededFuture(s + i + b)))
      .then(run(str -> {
        assertThat(context, str, is(RESULT_MSG + RESULT_INT + RESULT_BOOL));
        async.complete();
      }))
      .then(ifFailedRun(context::fail));
  }

  @Test
  public void canDestructure4(TestContext context) {
    Async async = context.async();
    when(succeededFuture(RESULT_MSG), succeededFuture(RESULT_INT),
      succeededFuture(RESULT_BOOL), succeededFuture(RESULT_MSG))
      .then(call((s, i, b, s2) -> succeededFuture(s + i + b + s2)))
      .then(run(str -> {
        assertThat(context, str, is(RESULT_MSG + RESULT_INT + RESULT_BOOL + RESULT_MSG));
        async.complete();
      }))
      .then(ifFailedRun(context::fail));
  }

  @Test
  public void flatMapFromSuccessfulChain(TestContext context) {
    Async async = context.async();

    when(succeededFuture(Arrays.asList(1, 2, 3, 4, 5)))
      .then(flatMap(i -> succeededFuture(i + 1)))
      .then(run(result -> {
        int sum = result.stream().mapToInt(Integer::intValue).sum();
        assertThat(context, sum, is(20));
        async.complete();
      }))
      .then(ifFailedRun(context::fail));
  }

  @Test
  public void flatMapHasException(TestContext context) {
    Async async = context.async();

    when(succeededFuture(Arrays.asList(1, 2, 3, 4, 5)))
      .then(flatMap(i -> failedFuture(FAILED)))
      .then(run(result -> context.fail("should never get here")))
      .then(ifFailedRun(err -> {
        assertThat(context, err.getMessage(), is(FAILED));
        async.complete();
      }));
  }

  @Test
  public void flatMapDoesNotExecuteIfChainIsFailed(TestContext context) {
    Async async = context.async();
    when(succeededFuture(Arrays.asList(1, 2, 3, 4, 5)))
      .then(run(() -> { throw new RuntimeException(FAILED); }))
      .then(flatMap(i -> succeededFuture(i + 1)))
      .then(run(() -> context.fail()))
      .then(ifFailedRun(err -> async.complete()));
  }
}
