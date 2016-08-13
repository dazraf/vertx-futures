package io.dazraf.vertx.futures.tuple;

import io.dazraf.vertx.futures.consumer.Consumer3;
import io.dazraf.vertx.futures.function.Function3;
import io.vertx.core.CompositeFuture;

public class Tuple3<T1, T2, T3> extends Tuple<Tuple3<T1, T2, T3>> {
  private final T1 t1;
  private final T2 t2;
  private final T3 t3;

  public Tuple3(T1 t1, T2 t2, T3 t3) {
    this.t1 = t1;
    this.t2 = t2;
    this.t3 = t3;
  }

  public Tuple3(CompositeFuture compositeFuture) {
    assert(compositeFuture.succeeded());
    this.t1 = compositeFuture.result(0);
    this.t2 = compositeFuture.result(1);
    this.t3 = compositeFuture.result(1);
  }

  public T1 getT1() {
    return t1;
  }

  public T2 getT2() {
    return t2;
  }

  public T3 getT3() {
    return t3;
  }

  public void accept(Consumer3<T1, T2, T3> consumer) {
    consumer.accept(t1, t2, t3);
  }

  public <R> R apply(Function3<T1, T2, T3, R> function) {
    return function.apply(t1, t2, t3);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Tuple3<?, ?, ?> tuple3 = (Tuple3<?, ?, ?>) o;

    if (t1 != null ? !t1.equals(tuple3.t1) : tuple3.t1 != null) return false;
    if (t2 != null ? !t2.equals(tuple3.t2) : tuple3.t2 != null) return false;
    return t3 != null ? t3.equals(tuple3.t3) : tuple3.t3 == null;

  }

  @Override
  public int hashCode() {
    int result = t1 != null ? t1.hashCode() : 0;
    result = 31 * result + (t2 != null ? t2.hashCode() : 0);
    result = 31 * result + (t3 != null ? t3.hashCode() : 0);
    return result;
  }
}
