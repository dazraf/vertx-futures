package io.dazraf.vertx.tuple;

import io.dazraf.vertx.consumer.Consumer2;
import io.dazraf.vertx.function.Function2;
import io.vertx.core.CompositeFuture;

/**
 * A tuple of two values of type {@link T1} and {@link T2}
 * @param <T1> The type of the first value in the tuple
 * @param <T2> The type of the second value in the tuple
 */
public class Tuple2<T1, T2> extends Tuple<Tuple2<T1, T2>> {
  private final T1 t1;
  private final T2 t2;

  /**
   * Construct using values for the two types
   * @param t1 the value of the first type
   * @param t2 the value of the second type
   */
  public Tuple2(T1 t1, T2 t2) {
    this.t1 = t1;
    this.t2 = t2;
  }

  /**
   * Construct from a <i>completed</i> future
   * @param compositeFuture a composite future that should contain two values of types that are assignable to this types generic parameters
   */
  public Tuple2(CompositeFuture compositeFuture) {
    assert(compositeFuture.succeeded());
    this.t1 = compositeFuture.result(0);
    this.t2 = compositeFuture.result(1);
  }

  /**
   * Retrieve the first value
   * @return the value of the first type
   */
  public T1 getT1() {
    return t1;
  }

  /**
   * Retrieve the second value
   * @return the value of the second type
   */
  public T2 getT2() {
    return t2;
  }

  /**
   * Calls a {@link Consumer2} function with the two values of this tuple. Any exceptions are propagated up the stack.
   * @param consumer The consumer function, that will be called with two values from this tuple.
   */
  public void accept(Consumer2<T1, T2> consumer) {
    consumer.accept(t1, t2);
  }

  /**
   * Calls a {@link Function2} function with the two values of the tuple and returns the result of that function.
   * Any exceptions are propagated up the stack.
   * @param function the function, that will be called with the two values from this tuple and returns a result of type {@link R}
   * @param <R> The type of the result returned from <code>#function</code>
   * @return The result from calling the function
   */
  public <R> R apply(Function2<T1, T2, R> function) {
    return function.apply(t1, t2);
  }

  /**
   * Implements deep equality of this object
   * @param o right hand sideof the equality
   * @return true iff <code>this</code>is equal to <code>o</code>
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;

    if (t1 != null ? !t1.equals(tuple2.t1) : tuple2.t1 != null) return false;
    return t2 != null ? t2.equals(tuple2.t2) : tuple2.t2 == null;

  }

  /**
   * Implements deep hashcode of this object
   * @return hashcode
   */
  @Override
  public int hashCode() {
    int result = t1 != null ? t1.hashCode() : 0;
    result = 31 * result + (t2 != null ? t2.hashCode() : 0);
    return result;
  }
}
