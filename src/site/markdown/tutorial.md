# Tutorials

## Useful static imports

`vertx-futures` has a small number of classes for its entry-points. 
For legibility, it's best to statically import these.

```java  
  import static io.dazraf.vertx.tuple.Tuple.*; // for creating typesafe structures of results
  import static io.dazraf.vertx.futures.FutureChain.*; // for creating graphs of futures
  import static io.dazraf.vertx.futures.http.HttpFutures.*; // for working with vert.x http APIs
```

---

## Calling a webservice

```java  
  @Test
  public void simpleGetTest(TestContext context) {
    Async async = context.async();

    when(future(httpClient.get("/")).end())
      .onSuccess(HttpFutures::checkHttpSuccess)
      .then2(response -> all(succeededFuture(response), bodyObject(response)))
      .onSuccess((response, body) -> assertThat(context, body.containsKey("time"), is(true)))
      .onSuccess((response, body) -> LOG.info("Response {} body checks out: {}", response.statusCode(), body.encode()))
      .onSuccess(async::complete)
      .onFail((Runnable) context::fail);
  }
```

Here, we write a unit test to call a Vert.x [`HttpClient`](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClient.html) with request to `GET /`.

We convert the [`HttpClientRequest`](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClientRequest.html) into a [`Future`](http://vertx.io/docs/apidocs/io/vertx/core/Future.html) using the
 [`future()`](apiDocs/io/dazraf/vertx/futures/http/HttpFutures.html#future-io.vertx.core.http.HttpClientRequest-) factory method.

Once we have a `Future` we can start chaining its state to subsequent operations.

In this chase, if the http server responds, we:

* check for a successful status code
* convert the body to a [`JsonObject`](http://vertx.io/docs/apidocs/io/vertx/core/json/JsonObject.html)
* check that the structure of the `JsonObject` is sound
* **Note** that we broadly speaking focused on the happy path. The handling and passing of failure conditions is handled automatically by the library. 
* if anywhere in the above code we have any failures untrapped errors, we fail the tests

---

## A Galaxy Far Far Away ... 

We're going to take the above and use it to access the awesome [Star Wars API](http://swapi.co).

We'll setup our test so that the `HttpClient` is configured to use `http://swapi.co` as the default host and port.

Then we'll encapsulate what we discussed above as a handy function to retrieve the JsonObject from any GET request:

```
    private Future<JsonObject> getJsonObject(String resource) {
    return when(future(httpClient.get(resource)).end())
      .onSuccess(HttpFutures::checkHttpSuccess)
      .then(HttpFutures::bodyObject);
  }
```

Now, the StarWars API services uses pagination, so we'll need to recursively retrieve all pages:

```
  private Future<JsonObject> getJsonObject(String resource) {
    return when(future(httpClient.get(resource)).end())
      .onSuccess(HttpFutures::checkHttpSuccess)
      .then(HttpFutures::bodyObject)
      .then(partialResult -> {
        String next = partialResult.getString("next");
        if (next != null) { // if we have more pages
          final URI uri = URI.create(next); // get the path and query
          String path = uri.getPath() + "?" + uri.getQuery();
          return when(getJsonObject(path)) // reinvoke this method
            .map(remainder -> { // add the remaining items to the final result
              partialResult.getJsonArray("results").addAll(remainder.getJsonArray("results"));
              return partialResult;
            });
        } else {
          return succeededFuture(partialResult);
        }
      });
  }
```

To show how that works, we'll write a test to get a list of all Star Wars film titles.

```java
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
```

... More tutorial