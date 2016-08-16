package io.dazraf.vertx.futures.http;

import io.vertx.core.Future;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static io.dazraf.vertx.futures.FutureChain.*;
import static io.dazraf.vertx.futures.TestUtils.assertThat;
import static io.dazraf.vertx.futures.http.HttpFutures.*;
import static io.dazraf.vertx.futures.tuple.Tuple.all;
import static org.hamcrest.CoreMatchers.*;
import static org.slf4j.LoggerFactory.getLogger;
import static io.vertx.core.Future.*;

@RunWith(VertxUnitRunner.class)
public class HttpTests {
  private static final Logger LOG = getLogger(HttpTests.class);

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
      .onSuccess(HttpFutures::checkHttpSuccess)
      .then2(response -> all(succeededFuture(response), bodyObject(response)))
      .onSuccess((response, body) -> assertThat(context, body.containsKey("time"), is(true)))
      .map((response, body) -> response)
      .onSuccess(response -> LOG.info("Response {} body checks out", response.statusCode()))
      .onSuccess(async::complete)
      .onFail((Runnable) context::fail);
  }

  private void getData(RoutingContext rc) {
    JsonObject data = new JsonObject();
    data.put("time", LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE));
    rc.response()
      .setChunked(true)
      .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
      .end(data.encode());

  }
}
