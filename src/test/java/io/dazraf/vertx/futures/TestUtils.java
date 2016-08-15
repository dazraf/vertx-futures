package io.dazraf.vertx.futures;

import io.vertx.core.Future;
import io.vertx.ext.unit.TestContext;
import org.hamcrest.Matcher;
import org.junit.Assert;

import static io.vertx.core.Future.*;

/**
 * Utility functions for tests
 */
public class TestUtils {
  public static final String FAIL_MSG = "error!";
  public static final String RESULT_MSG = "result!";
  public static final int RESULT_INT = 42;
  public static final boolean RESULT_BOOL = true;

  public static Future<String> aSucceededFuture() {
    return succeededFuture(RESULT_MSG);
  }
  public static Future<Object> aFailedFuture() {
    return failedFuture(FAIL_MSG);
  }

  public static <T> void assertThat(TestContext context, T actual, Matcher<? super T> matcher) {
    try {
      Assert.assertThat(actual, matcher);
    } catch (Throwable err) {
      context.fail(err);
    }
  }
}
