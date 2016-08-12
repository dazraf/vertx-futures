package io.dazraf.vertx.futures.tuple;

import io.dazraf.vertx.futures.consumer.Consumer2;
import io.dazraf.vertx.futures.function.Function2;
import io.vertx.core.CompositeFuture;

/**
 *
 * N.B. Mutable data structure
 * @param <T1>
 * @param <T2>
 */
public class Tuple2<T1, T2> extends Tuple<Tuple2<T1, T2>> {
  private T1 t1;
  private T2 t2;

  public Tuple2() {

  }

  public Tuple2(CompositeFuture compositeFuture) {
    set(compositeFuture);
  }

  public T1 getT1() {
    return t1;
  }

  public T2 getT2() {
    return t2;
  }

  public void accept(Consumer2<T1, T2> consumer) {
    consumer.accept(t1, t2);
  }

  public <R> R apply(Function2<T1, T2, R> function) {
    return function.apply(t1, t2);
  }

  public Tuple2<T1, T2> set(CompositeFuture result) {
    assert(result.succeeded());
    this.t1 = result.result(0);
    this.t2 = result.result(1);
    return this;
  }

}
