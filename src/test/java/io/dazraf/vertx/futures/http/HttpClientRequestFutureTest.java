package io.dazraf.vertx.futures.http;

import io.dazraf.vertx.HttpServerWrapper;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeoutException;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import static io.dazraf.vertx.HttpServerWrapper.createHttpServer;
import static io.dazraf.vertx.SocketTestUtils.getFreePort;
import static io.dazraf.vertx.futures.Futures.when;
import static io.dazraf.vertx.futures.http.HttpFutures.bodyObject;
import static io.dazraf.vertx.futures.http.HttpFutures.checkHttpSuccess;
import static io.dazraf.vertx.futures.http.HttpFutures.httpFuture;
import static io.dazraf.vertx.futures.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static io.dazraf.vertx.futures.processors.CallProcessor.call;
import static io.dazraf.vertx.futures.processors.IfProcessor.ifFailed;
import static io.dazraf.vertx.futures.processors.MapProcessor.map;
import static io.dazraf.vertx.futures.processors.RunProcessor.run;
import static io.dazraf.vertx.futures.processors.RunProcessor.runOnFail;
import static io.dazraf.vertx.futures.processors.RunProcessor.runOnResponse;
import static io.vertx.core.Future.future;
import static io.vertx.core.buffer.Buffer.buffer;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(VertxUnitRunner.class)
public class HttpClientRequestFutureTest {
  @Rule
  public Timeout rule = Timeout.seconds(40);

  @ClassRule
  public static RunTestOnContext contextRules = new RunTestOnContext();

  private static final Logger LOG = LoggerFactory.getLogger(HttpClientRequestFutureTest.class);

  private static final String MESSAGE_OK = "OK";
  private static final String MESSAGE_RESUME = "resume";

  private static Vertx vertx;
  private static HttpClient httpClient;

  @BeforeClass
  public static void staticSetup() {
    vertx = contextRules.vertx();
    httpClient = vertx.createHttpClient();
  }

  @AfterClass
  public static void staticTearDown(TestContext context) {
    LOG.info("tearing down");
    httpClient.close();
    httpClient = null;
    Handler<AsyncResult<Void>> asyncResultHandler = context.asyncAssertSuccess();
    vertx.close(ar -> {
      LOG.info("torn down");
      asyncResultHandler.handle(ar);
    });
    vertx = null;
  }

  @Before
  public void setup() {
  }

  @After
  public void teardown() {
  }

  @Test
  public void httpGETUnboundPortReturnsError(TestContext testContext) {
    LOG.info("Running httpGETUnboundPortReturnsError");
    Async async = testContext.async();

    when(httpFuture(httpClient.get(getFreePort(), "localhost", "/unknown/resource")).end())
        .then(run(() -> testContext.fail("http request should not have succeeded")))
        .then(runOnFail(throwable -> async.complete()));
  }

  @Test
  public void httpGETShouldSucceed(TestContext testContext) {
    LOG.info("Running httpGETShouldSucceed");
    Async async = testContext.async();
    Future<HttpServerWrapper> serverWrapper = createHttpServer(vertx);
    when(serverWrapper)
        .then(call(server -> httpFuture(httpClient.get(server.port(), "localhost", "/")).end()))
        .then(call(HttpFutures::bodyAsString))
        .then(run(result -> testContext.assertEquals(MESSAGE_OK, result)))
        .then(run(async::complete))
        .then(runOnFail(testContext::fail))
        .then(ar -> serverWrapper.result().close());
  }

  @Test
  public void httpGETShouldSucceedAndResponseIsAccessible(TestContext testContext) {
    LOG.info("Running httpGETShouldSucceedAndResponseIsAccessible");
    Async async = testContext.async();
    Future<HttpServerWrapper> serverWrapper = createHttpServer(vertx);
    when(serverWrapper)
        .then(call(server -> {
          HttpClientRequestWithFutureResponse request =
              httpFuture(httpClient.get(server.port(), "localhost", "/")).end();
          return when(request)
              .then(run(response -> testContext.assertEquals(request.result(), response)));
        }))
        .then(runOnFail(testContext::fail))
        .then(response -> serverWrapper.result().close())
        .then(run(async::complete));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldNotBeAbleToCallComplete() {
    LOG.info("Running shouldNotBeAbleToCallComplete");
    httpFuture(httpClient.get(1000, "localhost", "/")).complete();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldNotBeAbleToCallCompleteWithValue() {
    LOG.info("Running shouldNotBeAbleToCallCompleteWithValue");
    httpFuture(httpClient.get(1000, "localhost", "/")).complete(null);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldNotBeAbleToCallFail() {
    LOG.info("Running shouldNotBeAbleToCallFail");
    httpFuture(httpClient.get(1000, "localhost", "/")).fail("Fail");
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldNotBeAbleToCallFailWithThrowable() {
    LOG.info("Running shouldNotBeAbleToCallFailWithThrowable");
    httpFuture(httpClient.get(1000, "localhost", "/")).fail(new RuntimeException("Fail"));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void shouldNotBeAbleToSetExceptionHandler(TestContext testContext) {
    LOG.info("Running shouldNotBeAbleToSetExceptionHandler");
    httpFuture(httpClient.get(1000, "localhost", "/"))
        .exceptionHandler(throwable -> testContext.fail("Should never reach this"));
  }

  @Test
  public void willFailIfHttpServerRespondsWithFailureCode(TestContext testContext) {
    LOG.info("Running willFailIfHttpServerRespondsWithFailureCode");
    Async async = testContext.async();
    when(
        createHttpServer(vertx, request -> request.response().setStatusCode(INTERNAL_SERVER_ERROR).end()))
        .then(call(server -> httpFuture(httpClient.get(server.port(), "localhost", "/asdlkfjsadlkfjwoejir")).end()))
        .then(run(HttpFutures::checkHttpSuccess))
        .then(runOnResponse(response -> {
          testContext.assertTrue(response.failed());
          HttpException httpException = (HttpException) response.cause();
          testContext.assertEquals(httpException.getResponse().statusCode(), INTERNAL_SERVER_ERROR);
        }))
        .then(runOnFail(throwable -> async.complete()))
        .then(run(response -> testContext.fail("should never succeed")));
  }

  @Test
  public void failedHttpRequestShouldReturnFailedAsTrue(TestContext testContext) {
    LOG.info("Running failedHttpRequestShouldReturnFailedAsTrue");
    Async async = testContext.async();

    when(createHttpServer(vertx, request -> request.response().setStatusCode(INTERNAL_SERVER_ERROR).end()))
        .then(call(server -> httpFuture(httpClient.get(server.port(), "localhost", "/")).end()))
        .then(run(HttpFutures::checkHttpSuccess))
        .then(runOnResponse(response -> {
          testContext.assertTrue(response.failed());
          if (response.succeeded()) {
            testContext.fail("should never get here");
          } else {
            async.complete();
          }
        }));
  }

  @Test
  public void canWriteRequestBody(TestContext testContext) {
    LOG.info("Running canWriteRequestBody");

    Future<HttpServerWrapper> httpServer = createHttpServer(vertx, request -> {
      Future<Buffer> bodyFuture = future();
      request.bodyHandler(bodyFuture::complete);
      when(bodyFuture).then(run(buffer -> request.response().setChunked(true).write(buffer).end()));
    });

    JsonObject payload = new JsonObject().put("a", 1);

    // two requests
    // 1. write a json request as a string
    Async asyncRequest1 = testContext.async();
    when(httpServer)
        .then(call(server -> {
          LOG.info("Request1: sending a JSON request as String");
          return httpFuture(httpClient.post(server.port(), "localhost", "/"))
              .setChunked(true)
              .write(payload.encode()) // write as string
              .end();
        }))
        .then(call(request -> {
          LOG.info("Request1: received response. Now waiting for body");
          return bodyObject(request);
        }))
        .then(run(json -> {
          LOG.info("Request1: checking json payload contents");
          testContext.assertEquals(json, payload);
        }))
        .then(run(() -> {
          LOG.info("Request1: completed");
          asyncRequest1.complete();
        }))
        .then(runOnFail(throwable -> {
          LOG.error("Request1: failed\\n" + throwable.getMessage());
          testContext.fail(throwable);
        }));

    // 2. write json as a buffer
    Async asyncRequest2 = testContext.async();
    when(httpServer)
        .then(call(server -> {
          LOG.info("Request2: sending a JSON request as String");
          return httpFuture(httpClient.post(server.port(), "localhost", "/"))
              .setChunked(true)
              .write(buffer(payload.encode())) // write as buffer
              .end();
        }))
        .then(call(request -> {
          LOG.info("Request2: received response. Now waiting for body");
          return bodyObject(request);
        }))
        .then(run(json -> {
          LOG.info("Request2: checking json payload contents");
          testContext.assertEquals(json, payload);
        }))
        .then(run(() -> {
          LOG.info("Request2: completed");
          asyncRequest2.complete();
        }))
        .then(runOnFail(throwable -> {
          LOG.error("Request2: failed\\n" + throwable.getMessage());
          testContext.fail(throwable);
        }));
  }

  @Test
  public void canEndWithRequestBody(TestContext testContext) {
    LOG.info("Running canWriteRequestBodyOnEndRequest");
    Future<HttpServerWrapper> httpServer = createHttpServer(vertx, request -> {
      Future<Buffer> bodyFuture = future();
      request.bodyHandler(bodyFuture::complete);
      when(bodyFuture).then(run(buffer -> request.response().setChunked(true).write(buffer).end()));
    });

    JsonObject payload = new JsonObject().put("a", 1);

    Async asyncRequest1 = testContext.async();

    // two requests
    // 1. write a json request as a string
    when(httpServer)
        .then(call(server -> {
          LOG.info("Request1: sending a JSON request as String");
          return httpFuture(httpClient.post(server.port(), "localhost", "/"))
              .setChunked(true)
              .end(payload.encode()); // write as string
        }))
        .then(call(response -> {
          LOG.info("Request1: received response. Now waiting for body");
          return bodyObject(response);
        }))
        .then(run(json -> {
          LOG.info("Request1: checking json payload contents");
          testContext.assertEquals(json, payload);
        }))
        .then(run(() -> {
          LOG.info("Request1: completed");
          asyncRequest1.complete();
        }))
        .then(runOnFail(throwable -> {
          LOG.error("Request1: failed\\n" + throwable.getMessage());
          testContext.fail(throwable);
        }));

    // 2. write json as a buffer
    Async asyncRequest2 = testContext.async();
    when(httpServer)
        .then(call(server -> {
          LOG.info("Request2: sending a JSON request as Buffer");
          return httpFuture(httpClient.post(server.port(), "localhost", "/"))
              .setChunked(true)
              .end(buffer(payload.encode()));
        }))
        .then(call(response -> {
          LOG.info("Request2: received response. Now waiting for body");
          return bodyObject(response);
        }))
        .then(run(json -> {
          LOG.info("Request2: checking json payload contents");
          testContext.assertEquals(json, payload);
        }))
        .then(run(() -> {
          LOG.info("Request2: completed");
          asyncRequest2.complete();
        }))
        .then(runOnFail(throwable -> {
          LOG.error("Request2: failed\\n" + throwable.getMessage());
          testContext.fail(throwable);
        }));
  }

  @Test
  public void canSetChunking() {
    LOG.info("Running canSetChunking");
    assertTrue(
        httpFuture(httpClient.get(0, "localhost", "/"))
            .setChunked(true)
            .isChunked());
  }

  @Test
  public void canRetrieveMethod() {
    LOG.info("Running canRetrieveMethod");
    assertEquals(httpFuture(httpClient.get(0, "localhost", "/")).method(), HttpMethod.GET);
  }

  @Test
  public void canRetrieveURI() {
    LOG.info("Running canRetrieveURI");
    assertEquals(httpFuture(httpClient.get(0, "localhost", "/")).uri(), "/");
  }

  @Test
  public void canSetAndRetrieveHeaders() {
    LOG.info("Running canSetAndRetrieveHeaders");
    String NAME1 = "name1";
    String NAME2 = "name2";
    String VALUE1 = "value1";
    String VALUE2 = "value2";
    List<String> values = asList(VALUE1, VALUE2);

    HttpClientRequestWithFutureResponse request = httpFuture(httpClient.get(0, "localhost", "/"))
        .putHeader(NAME1, VALUE1)
        .putHeader(NAME2, values);

    assertEquals(request.headers().size(), 2);
    assertEquals(request.headers().get(NAME1), VALUE1);
    assertEquals(request.headers().getAll(NAME2), values);
  }

  @Test
  public void canSetAndRetrieveHeadersAsCharSequence() {
    LOG.info("Running canSetAndRetrieveHeadersAsCharSequence");
    CharSequence NAME1 = "name1";
    CharSequence NAME2 = "name2";
    CharSequence VALUE1 = "value1";
    CharSequence VALUE2 = "value2";
    List<CharSequence> values = asList(VALUE1, VALUE2);

    HttpClientRequestWithFutureResponse request = httpFuture(httpClient.get(0, "localhost", "/"))
        .putHeader(NAME1, VALUE1)
        .putHeader(NAME2, values);

    assertEquals(request.headers().size(), 2);
    assertEquals(request.headers().get(NAME1), VALUE1);
    assertEquals(request.headers().getAll(NAME2), values);
  }

  @Test
  public void canSetTimeout(TestContext testContext) {
    LOG.info("Running canSetTimeout");
    Future<HttpServerWrapper> httpServer = createHttpServer(vertx, request -> {
      vertx.setTimer(500, id -> request.response().end());
    });

    Async async = testContext.async();
    when(httpServer)
        .then(call(server -> httpFuture(httpClient.get(server.port(), "localhost", "/")).setTimeout(1)))
        .then(runOnFail(throwable -> {
          testContext.assertTrue(throwable instanceof TimeoutException);
          async.complete();
        }))
        .then(run(() -> {
          testContext.fail("call should not succeed due to timeout");
        }));
  }
  

  @Test
  public void canSendBodyOnEndWithDifferentEncoding(TestContext testContext) {
    LOG.info("Running canSendBodyOnEndWithDifferentEncoding");
    String TEST = new JsonObject().put("a", 1).encode();
    String encoding = "UTF-16";
    Future<HttpServerWrapper> httpServer = createHttpServer(vertx, request -> {
      request.bodyHandler(buffer -> {
        String body = buffer.toString(encoding);
        request.response().end(body, encoding);
      });
    });

    Async async = testContext.async();
    when(httpServer)
        .then(call(server -> httpFuture(httpClient.post(server.port(), "localhost", "/")).end(TEST, encoding)))
        .then(call(HttpFutures::body))
        .then(map(buffer -> buffer.toString(encoding)))
        .then(run(result -> {
          testContext.assertEquals(result, TEST);
          async.complete();
        }))
        .then(runOnFail(testContext::fail));
  }

  @Test
  public void canSendBodyWithDifferentEncoding(TestContext testContext) {
    LOG.info("Running canSendBodyWithDifferentEncoding");
    String TEST = new JsonObject().put("a", 1).encode();
    String encoding = "UTF-16";
    Future<HttpServerWrapper> httpServer = createHttpServer(vertx, request -> {
      request.bodyHandler(buffer -> {
        String body = buffer.toString(encoding);
        request.response().setChunked(true).write(body, encoding).end();
      });
    });

    Async async = testContext.async();
    when(httpServer)
        .then(call(server -> httpFuture(httpClient.post(server.port(), "localhost", "/"))
            .setChunked(true)
            .write(TEST, encoding).end()))
        .then(call(HttpFutures::body))
        .then(map(buffer -> buffer.toString(encoding)))
        .then(run(result -> {
          testContext.assertEquals(result, TEST);
          async.complete();
        }))
        .then(runOnFail(testContext::fail));
  }

  @Test
  public void canPauseAndResume(TestContext testContext) {
    LOG.info("Running canPauseAndResume");
    String TEST = new JsonObject().put("a", 1).encode();
    String encoding = "UTF-16";
    Future<HttpServerWrapper> httpServer = createHttpServer(vertx, request -> {
      request.bodyHandler(buffer -> {
        String body = buffer.toString(encoding);
        request.response().setChunked(true).write(body, encoding).end();
      });
    });

    Async async = testContext.async();
    when(httpServer)
        .then(call(server -> httpFuture(httpClient.post(server.port(), "localhost", "/"))
            .setChunked(true)
            .write(TEST, encoding)
            .pause()
            .resume()
            .end()))
        .then(call(HttpFutures::body))
        .then(map(buffer -> buffer.toString(encoding)))
        .then(run(result -> {
          testContext.assertEquals(result, TEST);
          async.complete();
        }))
        .then(runOnFail(testContext::fail));
  }

  /**
   * This is a relatively complex HTTP test to get sensible coverage of the WriteQueue functionality
   * Setup: Client and Server with back-pressure and reduced writeQueue length.
   * Test: After the first write, the writeQueue must be exhausted.
   *
   * To correctly setup this scenario:
   * 1. the server supports HTTP 100 Continue semantics
   * 2. on request, the server signals back with a continuation code
   * 3. which then triggers the client to perform the send
   * 4. it then immediately pauses the HTTP stream processing which ensures that the writeQueue on the client-side
   * builds up
   *
   * The reason why we do all these shenanigans is because the vert.x write-queue is in fact the netty connection
   * channel writability flag - none of which is actually created by vert.x until actually really needed. Quite reasonable but
   * this makes testing this feature particularly involved.
   *
   * @param context test context
   */
  @Test
  @Ignore
  public void canSetWriteQueueMaxSize(TestContext context) {
    LOG.info("Running canSetWriteQueueMaxSize");
    String payload = "this is the payload";
    Async async = context.async(2);

    when(createOrchestratedHttpServerWithBackPressure(async))
        .then(call(server -> createWriteQueueTestingClient(context, async, payload, server)));
  }

  private Future<HttpServerWrapper> createOrchestratedHttpServerWithBackPressure(Async async) {
    return createHttpServer(vertx, request -> {
      LOG.info("SERVER: received request");
      request.pause();
      request.endHandler(v -> {
        LOG.info("SERVER: request ended");
        request.response().end();
        async.countDown();
      });
      request.handler(buffer -> {
        LOG.info("SERVER: received '{}'", buffer.toString());
      });
      vertx.eventBus().consumer(MESSAGE_RESUME).handler(m -> request.resume());
      request.response().writeContinue();
    });
  }

  private Future<HttpClientResponse> createWriteQueueTestingClient(TestContext context, Async async, String payload,
                                                                   HttpServerWrapper server) {
    HttpClientRequestWithFutureResponse request = httpFuture(httpClient.post(server.port(), "localhost", "/"))
        .setWriteQueueMaxSize(0)
        .putHeader("Content-Length", Integer.toString(payload.length()))
        .putHeader("Expect", "100-Continue")
        .sendHead()
        .drainHandler(v -> LOG.info("CLIENT: drain handler invoked"))
        .endHandler(v -> {
          LOG.info("CLIENT: response ended");
          async.countDown();
        });
    request.continueHandler(v -> write(payload, request, context));
    return request;
  }

  private void write(String payload, HttpClientRequestWithFutureResponse request, TestContext testContext) {
    LOG.info("CLIENT: writing payload");
    request.write(payload);
    boolean queueFull = request.writeQueueFull();
    LOG.info("CLIENT: closing request");
    request.end();
    LOG.info("CLIENT: resume the server so that it may finish");
    vertx.eventBus().publish(MESSAGE_RESUME, null);
    if (queueFull) {
      LOG.info("CLIENT: reached write queue limit! this is good");
    } else {
      testContext.fail("did not reach queue limit! this is bad");
    }
  }
}
