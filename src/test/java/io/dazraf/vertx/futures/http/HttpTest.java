package io.dazraf.vertx.futures.http;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.dazraf.vertx.futures.VertxMatcherAssert;
import io.dazraf.vertx.tuple.Tuple;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import static io.dazraf.vertx.futures.Futures.when;
import static io.dazraf.vertx.futures.http.HttpFutures.bodyObject;
import static io.dazraf.vertx.futures.http.HttpFutures.future;
import static io.dazraf.vertx.futures.processors.CallProcessor.call;
import static io.dazraf.vertx.futures.processors.RunProcessor.ifFailedRun;
import static io.dazraf.vertx.futures.processors.RunProcessor.run;
import static io.vertx.core.Future.succeededFuture;
import static org.hamcrest.CoreMatchers.is;
import static org.slf4j.LoggerFactory.getLogger;

@RunWith(VertxUnitRunner.class)
public class HttpTest {

  private static final Logger LOG = getLogger(HttpTest.class);

  private Vertx vertx;
  private HttpServer httpServer;
  private int port = 8080;
  private HttpClient httpClient;

  @Before
  public void setup(TestContext context) {
    this.vertx = Vertx.vertx();
    Router router = Router.router(vertx);
    router.get("/").handler(this::getData);

    // TODO: get random available port

    this.httpServer = vertx.createHttpServer()
        .requestHandler(router::accept)
        .listen(port, context.asyncAssertSuccess());

    this.httpClient = vertx.createHttpClient(
        new HttpClientOptions()
            .setDefaultHost("localhost")
            .setDefaultPort(port)
    );
  }

  @After
  public void teardown(TestContext context) {
    httpClient.close();
    httpServer.close(context.asyncAssertSuccess());
    vertx.close(context.asyncAssertSuccess());
  }

  @Test
  public void simpleGetTest(TestContext context) {
    Async async = context.async();

    when(future(httpClient.get("/")).end())
        .then(run(HttpFutures::checkHttpSuccess))
        .then(call(response -> Tuple.tuple(succeededFuture(response), bodyObject(response))))
        .then(run((response, body) -> VertxMatcherAssert.assertThat(context, body.containsKey("time"), is(true))))
        .then(run((response, body) -> LOG
            .info("Response {} body checks out: {}", response.statusCode(), body.encode())))
        .then(run(async::complete))
        .then(ifFailedRun(context::fail));
  }

  @Test
  public void test_methods_unsupported() {
    final HttpClientRequestWithFutureResponse future = future(httpClient.get("/"));
    assertThrows(() -> future.complete(null), UnsupportedOperationException.class);
    assertThrows(future::complete, UnsupportedOperationException.class);
    assertThrows(() -> future.fail("error"), UnsupportedOperationException.class);
    assertThrows(() -> future.fail(new Exception("error")), UnsupportedOperationException.class);
    assertThrows(() -> future.exceptionHandler(err -> {}), UnsupportedOperationException.class);
    assertThrows(() -> future.handler(response -> {}), UnsupportedOperationException.class);
  }

  private void getData(RoutingContext rc) {
    JsonObject data = new JsonObject();
    data.put("time", LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE));
    rc.response()
        .setChunked(true)
        .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        .end(data.encode());

  }

  private void assertThrows(Runnable runnable, Class<? extends Throwable> throwableClass) {
    try {
      runnable.run();
      throwFailedToThrow(throwableClass);
    } catch (Throwable cause) {
      if (!throwableClass.isAssignableFrom(cause.getClass())) {
        throwFailedToThrow(throwableClass);
      }
    }
  }

  private void throwFailedToThrow(Class<? extends Throwable> throwableClass) {
    throw new RuntimeException("Failed to throw " + throwableClass.getName());
  }
}
