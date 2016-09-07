package io.dazraf.vertx.futures.http;

import io.dazraf.vertx.tuple.Tuple;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import javax.xml.ws.http.HTTPException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static io.dazraf.vertx.futures.Futures.*;
import static io.dazraf.vertx.futures.VertxMatcherAssert.*;
import static io.dazraf.vertx.futures.http.HttpFutures.*;
import static io.dazraf.vertx.futures.http.HttpFutures.future;
import static io.dazraf.vertx.futures.processors.CallProcessor.*;
import static io.dazraf.vertx.futures.processors.MapProcessor.*;
import static io.dazraf.vertx.futures.processors.RunProcessor.*;
import static io.vertx.core.Future.succeededFuture;
import static java.util.Arrays.*;
import static org.hamcrest.CoreMatchers.*;
import static org.slf4j.LoggerFactory.*;

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
    router.get("/object").handler(this::getData);
    router.get("/array").handler(this::getArray);
    router.get("/400").handler(this::get400);
    router.get("/badobject").handler(this::getBadObject);
    router.get("/badarray").handler(this::getBadArray);

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
  public void test_simpleGet(TestContext context) {
    Async async = context.async();

    when(future(httpClient.get("/object")).end())
        .then(run(HttpFutures::checkHttpSuccess))
        .then(call(response -> Tuple.tuple(succeededFuture(response), bodyObject(response))))
        .then(run((response, body) -> assertThat(context, body.containsKey("time"), is(true))))
        .then(run((response, body) -> LOG
            .info("Response {} body checks out: {}", response.statusCode(), body.encode())))
        .then(run(async::complete))
        .then(runOnFail(context::fail));
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

  @Test
  public void test_getBodyAsBuffer(TestContext context) {
    Async async = context.async();

    when(future(httpClient.get("/object")).end())
      .then(run(HttpFutures::checkHttpSuccess))
      .then(call(HttpFutures::body))
      .then(map(buffer -> buffer.toString()))
      .then(map(JsonObject::new))
      .then(run(body -> assertThat(context, body.containsKey("time"), is(true))))
      .then(run(async::complete))
      .then(runOnFail(context::fail));
  }

  @Test
  public void test_getBodyAsArray(TestContext context) {
    Async async = context.async();

    when(future(httpClient.get("/array")).end())
      .then(run(HttpFutures::checkHttpSuccess))
      .then(call(HttpFutures::bodyArray))
      .then(run(body -> assertThat(context, body.size(), is(3))))
      .then(run(async::complete))
      .then(runOnFail(context::fail));
  }

  @Test
  public void test_givenBadArray_fail(TestContext context) {
    Async async = context.async();

    when(future(httpClient.get("/badarray")).end())
      .then(run(HttpFutures::checkHttpSuccess))
      .then(call(HttpFutures::bodyArray))
      .then(runOnFail(err -> {
        context.assertTrue(DecodeException.class.isAssignableFrom(err.getClass()));
        async.complete();
      }))
      .then(run(() -> context.fail("should have failed")));
  }

  @Test
  public void test_givenBadObject_fail(TestContext context) {
    Async async = context.async();

    when(future(httpClient.get("/badobject")).end())
      .then(run(HttpFutures::checkHttpSuccess))
      .then(call(HttpFutures::bodyObject))
      .then(runOnFail(err -> {
        context.assertTrue(DecodeException.class.isAssignableFrom(err.getClass()));
        async.complete();
      }))
      .then(run(() -> context.fail("should have failed")));
  }

  @Test
  public void test_givenBadResponse_checkHttpSuccess_fails(TestContext context) {
    Async async = context.async();

    when(future(httpClient.get("/400")).end())
      .then(run(HttpFutures::checkHttpSuccess))
      .then(runOnFail(err -> {
        HTTPException httpException = (HTTPException)err;
        assertThat(context, httpException.getStatusCode(), is(400));
        assertThat(context, httpException.toString(), containsString("400"));
      }))
      .then(runOnFail(err -> async.complete()))
      .then(run(() -> context.fail("should never get here")));
  }

  private void getData(RoutingContext rc) {
    JsonObject data = new JsonObject();
    data.put("time", LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE));
    rc.response()
        .setChunked(true)
        .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        .end(data.encode());

  }

  private void getArray(RoutingContext rc) {
    JsonArray data = new JsonArray(asList(1, 2, 3));
    rc.response()
      .setChunked(true)
      .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
      .end(data.encode());
  }

  private void get400(RoutingContext rc) {
    rc.response().setStatusCode(400).setStatusMessage("Bad Robot").end();
  }


  private void getBadObject(RoutingContext rc) {
    rc.response()
      .setChunked(true)
      .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
      .end("badobject");
  }

  private void getBadArray(RoutingContext rc) {
    rc.response()
      .setChunked(true)
      .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
      .end("badarray");
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
