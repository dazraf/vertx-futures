package io.dazraf.vertx.function;

@FunctionalInterface
public interface Function2<T1, T2, R> extends FunctionN {
  R apply(T1 t1, T2 t2);
}
