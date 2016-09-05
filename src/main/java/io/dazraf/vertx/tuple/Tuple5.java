package io.dazraf.vertx.tuple;

import io.dazraf.vertx.consumer.Consumer5;
import io.dazraf.vertx.function.Function5;
import io.vertx.core.CompositeFuture;

/**
 * A tuple of three values of type {@link T1}, {@link T2} and {@link T3}
 * @param <T1> The type of the first value in the tuple
 * @param <T2> The type of the second value in the tuple
 * @param <T3> The type of the third value in the tuple
 * @param <T4> The type of the fourth value in the tuple
 * @param <T5> The type of the fifth value in the tuple
 */
public class Tuple5<T1, T2, T3, T4, T5> extends Tuple<Tuple5<T1, T2, T3, T4, T5>> {
  private final T1 t1;
  private final T2 t2;
  private final T3 t3;
  private final T4 t4;
  private final T5 t5;

  /**
   * Construct using values for the three types
   *
   * @param t1 the value of the first type
   * @param t2 the value of the second type
   * @param t3 the value of the third type
   * @param t4 the value of the fourth type
   * @param t5 the value of the fifth type
   */
  public Tuple5(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
    this.t1 = t1;
    this.t2 = t2;
    this.t3 = t3;
    this.t4 = t4;
    this.t5 = t5;
  }

  /**
   * Construct from a <i>completed</i> future
   *
   * @param compositeFuture a composite future that should contain three values of types that are assignable to this types generic parameters
   */
  public Tuple5(CompositeFuture compositeFuture) {
    assert (compositeFuture.succeeded());
    this.t1 = compositeFuture.result(0);
    this.t2 = compositeFuture.result(1);
    this.t3 = compositeFuture.result(2);
    this.t4 = compositeFuture.result(3);
    this.t5 = compositeFuture.result(4);
  }

  /**
   * Retrieve the first value
   *
   * @return the value of the first type
   */
  public T1 getT1() {
    return t1;
  }

  /**
   * Retrieve the second value
   *
   * @return the value of the second type
   */
  public T2 getT2() {
    return t2;
  }

  /**
   * Retrieve the third value
   *
   * @return the value of the third type
   */
  public T3 getT3() {
    return t3;
  }

  /**
   * Retrieve the fourth value
   *
   * @return the value for the fourth type
   */
  public T4 getT4() {
    return t4;
  }

  /**
   * Retrieve the fourth value
   *
   * @return the value for the fourth type
   */
  public T4 getT5() {
    return t4;
  }

  /**
   * Calls a {@link Consumer5} function with the five values of this tuple. Any exceptions are propagated up the stack.
   *
   * @param consumer The consumer function, that will be called with five values from this tuple.
   */
  public void accept(Consumer5<T1, T2, T3, T4, T5> consumer) {
    consumer.accept(t1, t2, t3, t4, t5);
  }

  /**
   * Calls a {@link Function5} function with the five values of the tuple and returns the result of that function.
   * Any exceptions are propagated up the stack.
   *
   * @param function the function, that will be called with five values from this tuple and returns a result of type {@link R}
   * @param <R>      The type of the result returned from <code>#function</code>
   * @return The result from calling the function
   */
  public <R> R apply(Function5<T1, T2, T3, T4, T5, R> function) {
    return function.apply(t1, t2, t3, t4, t5);
  }


  /**
   * Implements deep equality of this object
   *
   * @param o right hand sideof the equality
   * @return true iff <code>this</code>is equal to <code>o</code>
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Tuple5<?, ?, ?, ?, ?> tuple5 = (Tuple5<?, ?, ?, ?, ?>) o;

    if (t1 != null ? !t1.equals(tuple5.t1) : tuple5.t1 != null) return false;
    if (t2 != null ? !t2.equals(tuple5.t2) : tuple5.t2 != null) return false;
    if (t3 != null ? !t3.equals(tuple5.t3) : tuple5.t3 != null) return false;
    if (t4 != null ? !t4.equals(tuple5.t4) : tuple5.t4 != null) return false;
    return t5 != null ? t5.equals(tuple5.t5) : tuple5.t5 == null;

  }

  /**
   * Implements deep hashcode of this object
   *
   * @return hashcode
   */
  @Override
  public int hashCode() {
    int result = t1 != null ? t1.hashCode() : 0;
    result = 31 * result + (t2 != null ? t2.hashCode() : 0);
    result = 31 * result + (t3 != null ? t3.hashCode() : 0);
    result = 31 * result + (t4 != null ? t4.hashCode() : 0);
    result = 31 * result + (t5 != null ? t5.hashCode() : 0);
    return result;
  }
}

