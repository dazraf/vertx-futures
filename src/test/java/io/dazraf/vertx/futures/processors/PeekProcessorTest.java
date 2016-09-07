package io.dazraf.vertx.futures.processors;

import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.dazraf.vertx.TestUtils.*;
import static io.vertx.core.Future.*;
import static io.dazraf.vertx.futures.Futures.*;
import static io.dazraf.vertx.futures.VertxMatcherAssert.*;
import static io.dazraf.vertx.futures.processors.PeekProcessor.*;
import static io.dazraf.vertx.futures.processors.RunProcessor.*;
import static org.hamcrest.CoreMatchers.*;

@RunWith(VertxUnitRunner.class)
public class PeekProcessorTest {
  private static final String MSG = "message";
  private static final String FAILED_MSG = "failure!";
  @ClassRule
  public static RunTestOnContext rule = new RunTestOnContext();

  @Test
  public void test_exceptionInPeek_isSwallowed(TestContext context) {
    Async async = context.async();
    when(succeededFuture(MSG))
      .then(peek(s -> {
        throw new RuntimeException("failed");
      }))
      .then(run(async::complete))
      .then(runOnFail(context::fail));
  }


  @Test
  public void test_failure_canBePeeked(TestContext context) {
    Async async = context.async(2);
    when(failedFuture(FAILED_MSG))
      .then(peekFailure(err -> async.countDown()))
      .then(run((Runnable) context::fail))
      .then(runOnFail(err -> async.countDown()));
  }

  @Test
  public void canDestructure2(TestContext context) {
    Async async = context.async(2);
    when(succeededFuture(RESULT_MSG), succeededFuture(RESULT_INT))
      .then(peek((s, i) -> {
        assertThat(context, s, is(RESULT_MSG));
        assertThat(context, i, is(RESULT_INT));
        async.countDown();
      }))
      .then(run(async::countDown))
      .then(runOnFail(context::fail));
  }

  @Test
  public void canDestructure3(TestContext context) {
    Async async = context.async(2);
    when(succeededFuture(RESULT_MSG), succeededFuture(RESULT_INT), succeededFuture(RESULT_BOOL))
      .then(peek((s, i, b) -> {
        assertThat(context, s, is(RESULT_MSG));
        assertThat(context, i, is(RESULT_INT));
        assertThat(context, b, is(RESULT_BOOL));
        async.countDown();
      }))
      .then(run(async::countDown))
      .then(runOnFail(context::fail));
  }

  @Test
  public void canDestructure4(TestContext context) {
    Async async = context.async(2);
    when(succeededFuture(RESULT_MSG), succeededFuture(RESULT_INT),
      succeededFuture(RESULT_BOOL), succeededFuture(RESULT_MSG))
      .then(peek((s, i, b, s2) -> {
        assertThat(context, s, is(RESULT_MSG));
        assertThat(context, i, is(RESULT_INT));
        assertThat(context, b, is(RESULT_BOOL));
        assertThat(context, s2, is(RESULT_MSG));
        async.countDown();
      }))
      .then(run(async::countDown))
      .then(runOnFail(context::fail));
  }

  @Test
  public void canDestructure5(TestContext context) {
    Async async = context.async();
    when(succeededFuture(RESULT_MSG), succeededFuture(RESULT_INT),
      succeededFuture(RESULT_BOOL), succeededFuture(RESULT_MSG), succeededFuture(RESULT_BOOL))
      .then(peek((s, i, b, s2, b2) -> {
        assertThat(context, s, is(RESULT_MSG));
        assertThat(context, i, is(RESULT_INT));
        assertThat(context, b, is(RESULT_BOOL));
        assertThat(context, s2, is(RESULT_MSG));
        assertThat(context, b2, is(RESULT_BOOL));
        async.countDown();
      }))
      .then(run(async::countDown))
      .then(runOnFail(context::fail));
  }
}
