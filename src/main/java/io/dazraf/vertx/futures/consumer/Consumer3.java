package io.dazraf.vertx.futures.consumer;

@FunctionalInterface
public interface Consumer3<T1, T2, T3> extends ConsumerN {
  void accept(T1 t1, T2 t2, T3 t3);
}
