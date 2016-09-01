package io.dazraf.vertx.futures.tuple;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import io.dazraf.vertx.futures.processors.FutureProcessor;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;

/**
 * Base class for all Tuples. Essentially here to ensure type safety in classes such as {@link FutureProcessor}.
 * In addition provides the factory functions for creating Tuples from futures or intrinsic values.
 * @param <T> the type that is derived from this class.
 */
public abstract class Tuple<T extends Tuple> {

  /**
   * Create a {@link Tuple2} from two values
   * @param t1 The first value for the tuple
   * @param t2 The second value for the tuple
   * @param <T1> The type of the first value
   * @param <T2> The type of the second value
   * @return The constructed {@link Tuple2}
   */
  public static <T1, T2> Tuple2<T1, T2> all(T1 t1, T2 t2) {
    return new Tuple2<>(t1, t2);
  }

  /**
   * Create a {@link Tuple3} from two values
   * @param t1 The first value for the tuple
   * @param t2 The second value for the tuple
   * @param t3 The third value for the tuple
   * @param <T1> The type of the first value
   * @param <T2> The type of the second value
   * @param <T3> The type of the third value
   * @return The constructed {@link Tuple3}
   */
  public static <T1, T2, T3> Tuple3<T1, T2, T3> all(T1 t1, T2 t2, T3 t3) {
    return new Tuple3<>(t1, t2, t3);
  }

  /**
   * Create a {@link Tuple3} from two values
   * @param t1 The first value for the tuple
   * @param t2 The second value for the tuple
   * @param t3 The third value for the tuple
   * @param t4 The fourth value for the tuple
   * @param <T1> The type of the first value
   * @param <T2> The type of the second value
   * @param <T3> The type of the third value
   * @param <T4> The type of the fourth value
   * @return The constructed {@link Tuple4}
   */
  public static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> all(T1 t1, T2 t2, T3 t3, T4 t4) {
    return new Tuple4<>(t1, t2, t3, t4);
  }

  /**
   * Given two futures, of types {@link T1} and {@link T2} respectively, returns a Future of {@link Tuple2} for the
   * respective types {@link T1} and {@link T2}
   *
   * @param f1 The first future of type {@link T1}. The future may be complete or not
   * @param f2 The second future of type {@link T2}. The future may be complete or not
   * @param <T1> The parameterised type of the {@link Future} {@code f1}
   * @param <T2> The parameterised type of the {@link Future} {@code f2}
   * @return A {@link Future} that will resolve when both {@code f1} {@code f2} have resolved
   */
  public static <T1, T2> Future<Tuple2<T1, T2>> all(Future<T1> f1, Future<T2> f2) {
    return allFutureX(Arrays.asList(f1, f2), Tuple2::new);
  }

  /**
   * Given three futures, of types {@link T1}, {@link T2} and {@link T3} respectively, returns a Future of {@link Tuple2} for the
   * respective types {@link T1} {@link T2} and {@link T3}
   *
   * @param f1 The first future of type {@link T1}. The future may be complete or not
   * @param f2 The second future of type {@link T2}. The future may be complete or not
   * @param f3 The third future of type {@link T3}. The future may be complete or not
   * @param <T1> The parameterised type of the {@link Future} {@code f1}
   * @param <T2> The parameterised type of the {@link Future} {@code f2}
   * @param <T3> The parameterised type of the {@link Future} {@code f3}
   * @return A {@link Future} that will resolve when both {@code f1} {@code f2} {@code f3} have resolved
   */
  public static <T1, T2, T3> Future<Tuple3<T1, T2, T3>> all(Future<T1> f1, Future<T2> f2, Future<T3> f3) {
    return allFutureX(Arrays.asList(f1, f2, f3), Tuple3::new);
  }

  /**
   * Given three futures, of types {@link T1}, {@link T2}, {@link T3} and {@link T4} respectively, returns a Future of {@link Tuple2} for the
   * respective types {@link T1} {@link T2} and {@link T3}
   *
   * @param f1 The first future of type {@link T1}. The future may be complete or not
   * @param f2 The second future of type {@link T2}. The future may be complete or not
   * @param f3 The third future of type {@link T3}. The future may be complete or not
   * @param f4 The third future of type {@link T4}. The future may be complete or not
   * @param <T1> The parameterised type of the {@link Future} {@code f1}
   * @param <T2> The parameterised type of the {@link Future} {@code f2}
   * @param <T3> The parameterised type of the {@link Future} {@code f3}
   * @param <T4> The parameterised type of the {@link Future} {@code f4}
   * @return A {@link Future} that will resolve when both {@code f1} {@code f2} {@code f3} {@code f4} have resolved
   */
  public static <T1, T2, T3, T4> Future<Tuple4<T1, T2, T3, T4>> all(Future<T1> f1, Future<T2> f2, Future<T3> f3, Future<T4> f4) {
    return allFutureX(Arrays.asList(f1, f2, f3, f4), Tuple4::new);
  }

  private static <T extends Tuple<T>> Future<T> allFutureX(List<Future> futures, Function<CompositeFuture, T> adapter) {
    return CompositeFuture.all(futures).map(adapter);
  }
}
