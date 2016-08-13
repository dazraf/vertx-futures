package io.dazraf.vertx.futures.test;

import io.vertx.core.Future;

import static io.vertx.core.Future.*;

class TestUtils {
  static final String FAIL_MSG = "error!";
  static final String RESULT_MSG = "result!";

  static Future<String> aSucceededFuture() {
    return succeededFuture(RESULT_MSG);
  }

  static Future<Object> aFailedFuture() {
    return failedFuture(FAIL_MSG);
  }
}
