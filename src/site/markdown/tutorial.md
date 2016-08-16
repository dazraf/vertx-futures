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
  when(future(httpClient.get("/")).end())
    .onSuccess(HttpFutures::checkHttpSuccess)
    .then(response -> bodyObject(response))
    .onSuccess(body -> assertThat(context, body.containsKey("time"), is(true)))
    .onFail((Runnable) context::fail);
```

Here, we write a unit test to call a Vert.x `HttpClient` with request to `GET /`

We convert the `HttpClientRequest` into a `Future` using the
 [apiDocs/io/dazraf/vertx/futures/http/HttpFutures.html#future-io.vertx.core.http.HttpClientRequest-](`future()`) factory method.

Once we have a `Future` we can start chaining its state to subsequent operations.

In this chase, if the http server responds, we:

* check for a successful status code
* convert the body to a `JsonObject`
* check that the structure of the `JsonObject` is sound
* if anywhere in the above code we have any failures untrapped errors, we fail the tests

---

## A Galaxy Far Far Away ... 

We're going to take the above and use it to access the awesome [http://swapi.co](Star Wars API).

We'll setup our test so that the `HttpClient` is set to use `http://swapi.co` as the default host and port.

Then we'll encapsulate what we learn above as a handy function to retrieve the JsonObject from any GET request:

```java
    private Future<JsonObject> getJsonObject(String resource) {
    return when(future(httpClient.get(resource)).end())
      .onSuccess(HttpFutures::checkHttpSuccess)
      .then(HttpFutures::bodyObject);
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


Let's encapsulate a handy function to get any resource:

---

Section 2

---
