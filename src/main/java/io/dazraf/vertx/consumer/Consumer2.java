package io.dazraf.vertx.consumer;

@FunctionalInterface
public interface Consumer2<T1, T2> extends ConsumerN {
  void accept(T1 t1, T2 t2);
}
