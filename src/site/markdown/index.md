
`vertx-futures` is for users of [Vert.x](http://vertx.io/) in Java.

The library helps you write legible asynchronous logic with elegance and efficiency, using a syntax 
inspired by the [Promises/A+](https://promisesaplus.com/) specification.

It simplies complex [`AsyncResult<T>`](http://vertx.io/docs/apidocs/io/vertx/core/AsyncResult.html)
[`Handlers`](http://vertx.io/docs/apidocs/io/vertx/core/Handler.html), to fluent flows like this:

```java
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
```

#### Goals

* Efficient: for developers and computers
* Typesafe *Composition* **and** *Decomposition*
* Simple, extensible API: the main interface, `Futures` has only two methods `when` and `then`. 
* Keeps with the `io.vertx.core.Future` naming and semantics
