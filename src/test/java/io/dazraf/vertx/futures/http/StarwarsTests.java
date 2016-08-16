package io.dazraf.vertx.futures.http;

import io.dazraf.vertx.futures.FutureChain;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import java.util.stream.Collectors;

import static io.dazraf.vertx.futures.FutureChain.when;
import static io.dazraf.vertx.futures.http.HttpFutures.future;
import static org.slf4j.LoggerFactory.getLogger;

@RunWith(VertxUnitRunner.class)
public class StarwarsTests {
  private static final Logger LOG = getLogger(StarwarsTests.class);
  private Vertx vertx;
  private HttpClient httpClient;

  @Before
  public void setup(TestContext testContext) {
    this.vertx = Vertx.vertx();
    this.httpClient = vertx.createHttpClient(new HttpClientOptions().setDefaultHost("swapi.co").setDefaultPort(80));
  }

  @After
  public void teardown(TestContext testContext) {
    httpClient.close();
    vertx.close(testContext.asyncAssertSuccess());
  }

  @Test
  public void getFilms(TestContext testContext) {
    Async async = testContext.async();
    when(getJsonObject("/api/films/"))
      .map(jo -> jo.getJsonArray("results").stream()
        .map(obj -> ((JsonObject)obj))
        .map(obj -> obj.getString("title"))
        .collect(Collectors.toList()))
      .peekSuccess(list -> list.forEach(LOG::info))
      .onSuccess(() -> async.complete())
      .onFail(err -> testContext.fail(err));
  }

  private Future<JsonObject> getJsonObject(String resource) {
    return when(future(httpClient.get(resource)).end())
      .onSuccess(HttpFutures::checkHttpSuccess)
      .then(HttpFutures::bodyObject);
  }

  private Future<JsonArray> getJsonArray(String resource) {
    return when(future(httpClient.get(resource)).end())
      .onSuccess(HttpFutures::checkHttpSuccess)
      .then(HttpFutures::bodyArray);
  }

}
