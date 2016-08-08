package io.dazraf.vertx.futures.consumer;

@FunctionalInterface
public interface Consumer2<T1, T2> extends ConsumerN {
  void accept(T1 t1, T2 t2);
}
