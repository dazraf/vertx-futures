# Tutorials

## Useful static imports

`vertx-futures` has a small number of classes for its entry-points. 
For legibility, it's best to statically import these.

```
  import static io.dazraf.vertx.tuple.Tuple.*; // for creating typesafe structures of results
  import static io.dazraf.vertx.futures.FutureChain.*; // for creating graphs of futures
  import static io.dazraf.vertx.futures.http.HttpFutures.*; // for working with vert.x http APIs
```

---

## Calling a webservice

```
  @Test
  public void simpleGetTest(TestContext context) {
    Async async = context.async();

    when(future(httpClient.get("/")).end())
      .onSuccess(HttpFutures::checkHttpSuccess)
      .then2(response -> all(succeededFuture(response), bodyObject(response)))
      .onSuccess((response, body) -> assertThat(context, body.containsKey("time"), is(true)))
      .peekSuccess((response, body) -> LOG.info("Response {} body checks out: {}", response.statusCode(), body.encode())) // 
      .onSuccess(async::complete) // if succeed to get to this point, complete the test
      .onFail((Runnable) context::fail); // if anything fails, fails the test
  }
```

Here, we write a unit test to call a Vert.x [`HttpClient`](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClient.html) with request to `GET /`.

We convert the [`HttpClientRequest`](http://vertx.io/docs/apidocs/io/vertx/core/http/HttpClientRequest.html) 
into a [`Future`](http://vertx.io/docs/apidocs/io/vertx/core/Future.html) using the 
[`future()`](apiDocs/io/dazraf/vertx/futures/http/HttpFutures.html#future-io.vertx.core.http.HttpClientRequest-) 
factory method.

Once we have a `Future` we can start chaining its state to subsequent operations.

In this chase, if the http server responds, we:

* check for a successful status code
* convert the body to a [`JsonObject`](http://vertx.io/docs/apidocs/io/vertx/core/json/JsonObject.html) and return this 
together with the original response object using a [`Tuple.all()`](apidocs/io/dazraf/vertx/futures/tuple/Tuple.html#all-T1-T2-) factory method
* check that the structure of the `JsonObject` is sound
* if anywhere in the above code we have any untrapped errors, we fail the test.

*Please note* 

Broadly speaking, we've only had to focus on the "happy" path. The handling and passing of failure conditions done automatically by the library. 

We use [`Tuple.all()`](apidocs/io/dazraf/vertx/futures/tuple/Tuple.html#all-T1-T2-) to create a return object that combines both the `request` and the `body`. 
This is to demonstrate that multiple results can be passed out from one stage to the next; all typesafe!

Also, the above example shows the use of `onSuccess` and `peekSuccess`. 
In the API these methods work in exactly the same way, except that:

* [`onSuccess`](apidocs/io/dazraf/vertx/futures/FutureChain.html#onSuccess-java.util.function.Consumer-), 
[`onFail`](apidocs/io/dazraf/vertx/futures/FutureChain.html#onFail-java.util.function.Consumer-),
[`onComplete`](apidocs/io/dazraf/vertx/futures/FutureChain.html#onComplete-java.util.function.Consumer-) etc 
will cause the flow to fail if the passed in handler function throws an exception
* [`peekSuccess`](apidocs/io/dazraf/vertx/futures/FutureChain.html#peekSuccess-java.util.function.Consumer-), 
[`peekFail`](apidocs/io/dazraf/vertx/futures/FutureChain.html#peekFail-java.util.function.Consumer-), 
[`peekComplete`](apidocs/io/dazraf/vertx/futures/FutureChain.html#peekComplete-java.util.function.Consumer-) etc
will silently ignore any exception in the handler function (in fact, these are `trace`d to the logs, if enabled as such).


---

## A Galaxy Far Far Away ... 

We're going to take the above and use it to access the awesome [Star Wars API](http://swapi.co).

We'll setup our test so that the `HttpClient` is configured to use `http://swapi.co` as the default host and port.

Then we'll encapsulate what we discussed above as a handy function to retrieve the JsonObject from any GET request:

```
  private Future<JsonObject> httpGetJsonObject(String resource) {
    return when(future(httpClient.get(resource)).end())
      .onSuccess(HttpFutures::checkHttpSuccess)
      .then(HttpFutures::bodyObject);
  }

```

Now, the StarWars API services uses pagination, so we'll need to recursively retrieve all pages:

```
  private Future<JsonObject> httpGetJsonObject(String resource) {
    return when(future(httpClient.get(resource)).end())
      .onSuccess(HttpFutures::checkHttpSuccess)
      .then(HttpFutures::bodyObject)
      .then(result -> {
        String next = result.getString("next");
        if (next != null) {
          return getRemainingPages(result, next);
        } else {
          return succeededFuture(result);
        }
      });
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
```

To show how that works, we'll write a test to get a list of all Star Wars film titles.

```
  @Test
  public void getFilms(TestContext testContext) {
    Async async = testContext.async();
    when(httpGetJsonObject("/api/films/"))
      .map(jo -> jo.getJsonArray("results").stream()
        .map(obj -> ((JsonObject)obj))
        .map(obj -> obj.getString("title"))
        .collect(Collectors.toList()))
      .peekSuccess(list -> list.forEach(LOG::info))
      .onSuccess(() -> async.complete())
      .onFail(err -> testContext.fail(err));
  }
```

... More tutorial to follow ...