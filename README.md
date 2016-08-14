# vertx-futures

![graph](docs/graph.png)

__Work In Progress__

For users of Vert.x in Java.

Helps you write legible asynchronous logic with elegance, efficiency and flair.

No matter how complex the flow.

Inspired by the [Promises/A+](https://promisesaplus.com/) specification.

## Goals:

* Typesafe
* Efficient: for developers and computers
* Typesafe *Composition*
* Typesafe *Destructuring*

## Example

```java
    when(getId())
      .then2(id -> all(getName(id), getAge(id)))
      .then((name, age) -> composeMessage(name, age))
      .onSuccess(result -> LOG.info(result))
      .onFail(cause -> LOG.error("error handler", cause));

```

... more to follow ...


