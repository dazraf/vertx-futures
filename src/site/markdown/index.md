
`vertx-futures` is for users of [Vert.x](http://vertx.io/) in Java.

The library helps you write legible asynchronous logic with elegance and efficiency, using a syntax 
inspired by the [Promises/A+](https://promisesaplus.com/) specification.

It simplies complex [`AsyncResult<T>`](http://vertx.io/docs/apidocs/io/vertx/core/AsyncResult.html)
[`Handlers`](http://vertx.io/docs/apidocs/io/vertx/core/Handler.html), to elegant continuations like this:

```java
  @Test
  public void getAllStarshipsUsedByResidentsOfTatooine(TestContext testContext) {
    Async async = testContext.async();
    when(findPlanet("Tatooine"), getAllCharacters())
      .map((tatooine, characters) -> getResidents(tatooine, characters))
      .then(residents -> getUniqueStarshipsUsedByResidents(residents, getAllStarships()))
      .peekSuccess(starships -> starships.forEach(o -> LOG.info(o.toString())))
      .onSuccess(async::complete)
      .onFail(err -> testContext.fail(err));
  }
```

#### Goals

* Efficient: for developers and computers
* Typesafe *Composition* **and** *Decomposition*
* Simple API: `when`, `then`, `onSuccess`, `onFail`, `onComplete`, `peek`, `map` and `ifFailed`
* Keeps with the `io.vertx.core.Future` naming and semantics
