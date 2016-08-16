
With this library, you can simplify complex 
[`AsyncResult<T>`](http://vertx.io/docs/apidocs/io/vertx/core/AsyncResult.html)
[`Handlers`](http://vertx.io/docs/apidocs/io/vertx/core/Handler.html) with elegant continuations like this:

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