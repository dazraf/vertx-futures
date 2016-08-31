package io.dazraf.vertx.futures.http;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import static io.dazraf.vertx.futures.Futures.when;
import static io.dazraf.vertx.futures.http.HttpFutures.future;
import static io.dazraf.vertx.futures.processors.CallProcessor.*;
import static io.dazraf.vertx.futures.processors.MapProcessor.*;
import static io.dazraf.vertx.futures.processors.PeekProcessor.*;
import static io.dazraf.vertx.futures.processors.RunProcessor.*;

import static io.vertx.core.Future.succeededFuture;
import static org.slf4j.LoggerFactory.getLogger;

@Ignore // Acceptance testing
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
    when(httpGetJsonObject("/api/films/"))
      .then(map(jo -> jo.getJsonArray("results").stream()
        .map(obj -> ((JsonObject)obj))
        .map(obj -> obj.getString("title"))
        .collect(Collectors.toList())))
      .then(peek(list -> list.forEach(LOG::info)))
      .then(run(async::complete))
      .then(ifFailedRun(testContext::fail));
  }

  @Test
  public void getAllStarshipsUsedByResidentsOfTatooine(TestContext testContext) {
    Async async = testContext.async();
    when(findPlanet("Tatooine"), getAllCharacters())
      .then(map((tatooine, characters) -> getResidents(tatooine, characters)))
      .then(call(residents -> getUniqueStarshipsUsedByResidents(residents, getAllStarships())))
      .then(peek(starships -> starships.forEach(o -> LOG.info(o.toString()))))
      .then(run(async::complete))
      .then(ifFailedRun(testContext::fail));
  }

  private Future<List<JsonObject>> getUniqueStarshipsUsedByResidents(List<JsonObject> residents, Future<JsonArray> starShips) {

    Set<String> urls = residents.stream()
      .flatMap(jo -> jo.getJsonArray("starships").stream()
        .map(Object::toString))
      .collect(Collectors.toSet());

    return when(starShips)
      .map(this::buildIndex)
      .map(starships -> urls.stream().map(starships::get).collect(Collectors.toList()));
  }

  private List<JsonObject> getResidents(JsonObject planet, JsonArray characters) {
    final Map<String, JsonObject> characterIndex = buildIndex(characters);
    return planet.getJsonArray("residents")
      .stream()
      .map(Object::toString)
      .map(characterIndex::get)
      .collect(Collectors.toList());
  }

  private Map<String, JsonObject> buildIndex(JsonArray arrayOfObjects) {
    return arrayOfObjects.stream().map(o -> (JsonObject) o).collect(Collectors.toMap(o -> o.getString("url"), o -> o));
  }

  private Future<JsonObject> findPlanet(String planetName) {
    return when(getAllPlanets())
      .map(planets -> planets.stream()
      .map(obj -> (JsonObject)obj)
      .filter(jo -> planetName.equals(jo.getString("name")))
      .findFirst()
      .get());
  }

  private Future<JsonArray> getAllCharacters() {
    return httpGetJsonObject("/api/people/")
      .map(jo -> jo.getJsonArray("results"));
  }

  private Future<JsonArray> getAllPlanets() {
    return httpGetJsonObject("/api/planets/")
      .map(jo -> jo.getJsonArray("results"));
  }

  private Future<JsonArray> getAllStarships() {
    return httpGetJsonObject("/api/starships/")
      .map(jo -> jo.getJsonArray("results"));
  }

  private Future<JsonObject> httpGetJsonObject(String resource) {
    return when(future(httpClient.get(resource)).end())
      .then(run(HttpFutures::checkHttpSuccess))
      .then(call(HttpFutures::bodyObject))
      .then(call(result -> {
        String next = result.getString("next");
        if (next != null) {
          return getRemainingPages(result, next);
        } else {
          return succeededFuture(result);
        }
      }));
  }

  private Future<JsonObject> getRemainingPages(JsonObject result, String next) {
    final URI uri = URI.create(next);
    String path = uri.getPath() + "?" + uri.getQuery();
    return when(httpGetJsonObject(path)) //
      .map(remainder -> {
        result.getJsonArray("results").addAll(remainder.getJsonArray("results"));
        return result;
      });
  }
}
