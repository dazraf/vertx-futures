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
  private final T1 t1;
  private final T2 t2;

  public Tuple2(CompositeFuture compositeFuture) {
    assert(compositeFuture.succeeded());
    this.t1 = compositeFuture.result(0);
    this.t2 = compositeFuture.result(1);
  }

  public Tuple2(T1 t1, T2 t2) {
    this.t1 = t1;
    this.t2 = t2;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;

    if (t1 != null ? !t1.equals(tuple2.t1) : tuple2.t1 != null) return false;
    return t2 != null ? t2.equals(tuple2.t2) : tuple2.t2 == null;

  }

  @Override
  public int hashCode() {
    int result = t1 != null ? t1.hashCode() : 0;
    result = 31 * result + (t2 != null ? t2.hashCode() : 0);
    return result;
  }
}
