# Tutorials

## Useful static imports

`vertx-futures` has a small number of classes for its entry-points. 
For legibility, it's best to statically import these.

```java
    import static io.dazraf.vertx.futures.Futures.when;
    import static io.dazraf.vertx.futures.http.HttpFutures.future;
    import static io.dazraf.vertx.futures.processors.CallProcessor.*;
    import static io.dazraf.vertx.futures.processors.MapProcessor.*;
    import static io.dazraf.vertx.futures.processors.PeekProcessor.*;
    import static io.dazraf.vertx.futures.processors.RunProcessor.*;
```

---

## Calling a webservice

```
  @Test
  public void simpleGetTest(TestContext context) {
    Async async = context.async();

    when(future(httpClient.get("/")).end())
        .then(run(HttpFutures::checkHttpSuccess))
        .then(call(response -> all(succeededFuture(response), bodyObject(response))))
        .then(run((response, body) -> VertxMatcherAssert.assertThat(context, body.containsKey("time"), is(true))))
        .then(run((response, body) -> LOG
            .info("Response {} body checks out: {}", response.statusCode(), body.encode())))
        .then(run(async::complete))
        .then(ifFailedRun(context::fail));
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

Broadly speaking, we've only had to focus on the "happy" path. 
The handling and passing of failure conditions is carried out automatically by the library. 

We use [`Tuple.all()`](apidocs/io/dazraf/vertx/futures/tuple/Tuple.html#all-T1-T2-) to create a return object that combines both the `request` and the `body`. 
This is to demonstrate that multiple results can be passed out from one stage to the next; all typesafe!

Also, the above example shows the use of `FutureProcessors`. `vertx-futures` ships with the following:
* `CallProcessor` - takes the result(s) of the chain and returns another `Future`. The static overloaded method `call` will cover most cases. Any exceptions cause the chain to fail.
* `MapProcessor` - takes the result(s) and returns a new result (not a `Future`). Any exceptions cause the chain to fail.
* `PeekProcessor` - takes the result(s) of the chain and performs some set of operations on it, returning no result. An exceptions are ignored.
* `RunProcessor` - takes the result(s) of the chain and performs some set of operations on it, returning no result. Exceptions will cause the chain to fail.


---

## A Galaxy Far Far Away ... 

We're going to take the above and use it to access the awesome [Star Wars API](http://swapi.co).

We'll setup our test so that the `HttpClient` is configured to use `http://swapi.co` as the default host and port.

Then we'll encapsulate what we discussed above as a handy function to retrieve the JsonObject from any GET request:

```
  private Future<JsonObject> httpGetJsonObject(String resource) {
    return when(future(httpClient.get(resource)).end())
      .then(run(HttpFutures::checkHttpSuccess))
      .then(call(HttpFutures::bodyObject));
  }

```

Now, the StarWars API uses pagination, so we'll need to recursively retrieve all pages:

```
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
```

To show how that works, we'll write a test to get a list of all Star Wars film titles.

```
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
```

... More tutorial to follow ...