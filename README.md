# vertx-futures

![graph](docs/graph.png)

__Work In Progress__

For users of Vert.x in Java.

Helps you write legible asynchronous logic with elegance, efficiency and flair.
No matter how complex the flow.

Inspired by the [Promises/A+](https://promisesaplus.com/) specification.

## Goals:

* Typesafe
* Efficient for developers and computers
* Ability to create arbitrary compute graphs of futures i.e. ...
* Join multiple futures together in a type safe manner (using first-class tuples)
* Proper *Destructuring* of composite futures into lambdas of equivalent parameters


## Example

```java
    when(getId())
      .then2(id -> all(getName(id), getAge(id)))
      .then((name, age) -> composeMessage(name, age))
      .onSuccess(result -> LOG.info(result))
      .onFail(cause -> LOG.error("error handler", cause));

```

... more to follow ...


