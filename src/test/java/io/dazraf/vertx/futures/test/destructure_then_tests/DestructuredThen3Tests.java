package io.dazraf.vertx.futures.test.destructure_then_tests;

import io.dazraf.vertx.futures.tuple.Tuple3;
import org.junit.Test;

import static io.dazraf.vertx.futures.FutureChain.*;
import static io.dazraf.vertx.futures.test.TestUtils.*;
import static io.dazraf.vertx.futures.tuple.Tuple.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class DestructuredThen3Tests {
  @Test
  public void oneThenSuccessfulThreeTest() {
    final Tuple3<String, String, String> result =
      when(aSucceededFuture())
      .then3(s -> all(aSucceededFuture(), aSucceededFuture(), aSucceededFuture()))
      .result();
    assertThat(result, is(all(RESULT_MSG, RESULT_MSG, RESULT_MSG)));
  }

  @Test
  public void twoThenSuccessfulThreeTest() {
    final Tuple3<String, String, String> result =
      when(aSucceededFuture(), aSucceededFuture())
      .then3((s1, s2) -> {
        assertThat(s1, is(RESULT_MSG));
        assertThat(s2, is(RESULT_MSG));
        return all(aSucceededFuture(), aSucceededFuture(), aSucceededFuture());
      })
      .result();
    assertThat(result, is(all(RESULT_MSG, RESULT_MSG, RESULT_MSG)));
  }

  @Test
  public void threeThenSuccessfulThreeTest() {
    final Tuple3<String, String, String> result =
      when(aSucceededFuture(), aSucceededFuture(), aSucceededFuture())
        .then3((s1, s2, s3) -> {
          assertThat(s1, is(RESULT_MSG));
          assertThat(s2, is(RESULT_MSG));
          assertThat(s3, is(RESULT_MSG));
          return all(aSucceededFuture(), aSucceededFuture(), aSucceededFuture());
        })
        .result();
    assertThat(result, is(all(RESULT_MSG, RESULT_MSG, RESULT_MSG)));
  }
}
