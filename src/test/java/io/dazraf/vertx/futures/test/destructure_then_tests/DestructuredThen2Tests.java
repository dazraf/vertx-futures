package io.dazraf.vertx.futures.test.destructure_then_tests;

import io.dazraf.vertx.futures.tuple.Tuple2;
import org.junit.Test;

import static io.dazraf.vertx.futures.FutureChain.*;
import static io.dazraf.vertx.futures.test.TestUtils.*;
import static io.dazraf.vertx.futures.tuple.Tuple.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class DestructuredThen2Tests {
  @Test
  public void oneThenSuccessfulTwoTest() {
    final Tuple2<String, String> result =
      when(aSucceededFuture())
      .then2(s -> all(aSucceededFuture(), aSucceededFuture()))
      .result();
    assertThat(result, is(all(RESULT_MSG, RESULT_MSG)));
  }

  @Test
  public void twoThenSuccessfulTwoTest() {
    final Tuple2<String, String> result =
      when(aSucceededFuture(), aSucceededFuture())
      .then2((s1, s2) -> {
        assertThat(s1, is(RESULT_MSG));
        assertThat(s2, is(RESULT_MSG));
        return all(aSucceededFuture(), aSucceededFuture());
      })
      .result();
    assertThat(result, is(all(RESULT_MSG, RESULT_MSG)));
  }

  @Test
  public void threeThenSuccessfulTwoTest() {
    final Tuple2<String, String> result =
      when(aSucceededFuture(), aSucceededFuture(), aSucceededFuture())
        .then2((s1, s2, s3) -> {
          assertThat(s1, is(RESULT_MSG));
          assertThat(s2, is(RESULT_MSG));
          assertThat(s3, is(RESULT_MSG));
          return all(aSucceededFuture(), aSucceededFuture());
        })
        .result();
    assertThat(result, is(all(RESULT_MSG, RESULT_MSG)));
  }
}
