package io.dazraf.vertx.futures;

import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.slf4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.slf4j.LoggerFactory.*;

abstract class AbstractFutureChain<T, Derived extends AbstractFutureChain<T, Derived>>
  implements FutureChain<T, Derived> {

  private static final Logger LOG = getLogger(AbstractFutureChain.class);
  private final Future<T> future;
  private final List<Handler<AsyncResult<T>>> handlers = new CopyOnWriteArrayList<>();


  // --- FutureChain implementation ---

  @Override
  public <R> FutureChain1<R> then(Function<T, Future<R>> futureFunction) {
    Future<R> result = Future.future();
    setHandler(ar -> {
      if (ar.succeeded()) {
        futureFunction.apply(ar.result()).setHandler(result.completer());
      } else {
        result.fail(ar.cause());
      }
    });
    return FutureChain.when(result);
  }

  @Override
  public <T1, T2> FutureChain2<T1, T2> then(Function<T, Future<CompositeFuture>> futureFunction,
                                            Class<T1> c1, Class<T2> c2) {
    Future<CompositeFuture> future = getCompositeFutureOnCompositeFutureFn(futureFunction);
    return new FutureChain2<>(future);
  }

  private Future<CompositeFuture> getCompositeFutureOnCompositeFutureFn(Function<T, Future<CompositeFuture>> futureFunction) {
    Future<CompositeFuture> future = Future.future();
    setHandler(ar -> {
      if (ar.succeeded()) {
        futureFunction.apply(ar.result()).setHandler(ar2 -> {
          if (ar2.succeeded()) {
            future.complete(ar2.result());
          } else {
            future.fail(ar2.cause());
          }
        });
      } else {
        future.fail(ar.cause());
      }
    });
    return future;
  }

  @Override
  public <T1, T2, T3> FutureChain3<T1, T2, T3> then(Function<T, Future<CompositeFuture>> futureFunction,
                                             Class<T1> class1, Class<T2> class2, Class<T3> class3) {
    Future<CompositeFuture> future = getCompositeFutureOnCompositeFutureFn(futureFunction);
    return new FutureChain3<>(future);
  }


  @Override
  public <T1, T2, T3, T4> FutureChain4<T1, T2, T3, T4> then(Function<T, Future<CompositeFuture>> futureFunction,
                                                     Class<T1> class1, Class<T2> class2, Class<T3> class3, Class<T4> class4) {

    Future<CompositeFuture> future = getCompositeFutureOnCompositeFutureFn(futureFunction);
    return new FutureChain4<>(future);
  }


  @Override
  public Derived onSuccess(Consumer<T> consumer) {
    Future<T> result = Future.future();
    setHandler(ar -> {
      if (ar.succeeded()) {
        try {
          consumer.accept(ar.result());
          result.complete(ar.result());
        } catch (Throwable error) {
          result.fail(error);
        }
      } else {
        result.fail(ar.cause());
      }
    });
    return wrap(result);
  }


  @Override
  public Derived onError(Consumer<Throwable> consumer) {
    Future<T> result = Future.future();
    setHandler(ar -> {
      if (ar.failed()) {
        try {
          consumer.accept(ar.cause());
          result.fail(ar.cause());
        } catch (Throwable error) {
          result.fail(error);
        }
      }
    });
    return wrap(result);
  }

  @Override
  public Derived peek(Consumer<T> consumer) {
    Future<T> result = Future.future();
    setHandler(ar -> {
      if (ar.succeeded()) {
        try {
          consumer.accept(ar.result());
        } catch (Throwable error) {
          // throw away any errors in the peek consumer
        }
        // always pass on the result
        result.complete(ar.result());
      } else {
        result.fail(ar.cause());
      }
    });
    return wrap(result);
  }

  @Override
  public FutureChain1<Void> mapVoid() {
    Future<Void> result = Future.future();
    setHandler(ar -> {
      if (succeeded()) {
        result.complete();
      } else {
        result.fail(ar.cause());
      }
    });
    return FutureChain.when(result);
  }

  @Override
  public Derived onResult(Consumer<AsyncResult<T>> consumer) {
    Future<T> result = Future.future();
    setHandler(ar -> {
      try {
        consumer.accept(ar);
      } catch (Throwable error) {
        result.fail(error);
      }
    });
    return wrap(result);
  }

  // ---- Future Implementation ----

  @Override
  public boolean isComplete() {
    return future.isComplete();
  }

  @Override
  public Future<T> setHandler(Handler<AsyncResult<T>> handler) {
    addHandler(handler);
    return this;
  }


  @Override
  public void complete(T result) {
    notImplemented();
  }

  @Override
  public void complete() {
    notImplemented();
  }

  @Override
  public void fail(Throwable throwable) {
    notImplemented();
  }

  @Override
  public void fail(String failureMessage) {
    notImplemented();
  }

  @Override
  public T result() {
    return future.result();
  }

  @Override
  public Throwable cause() {
    return future.cause();
  }

  @Override
  public boolean succeeded() {
    return future.succeeded();
  }

  @Override
  public boolean failed() {
    return future.failed();
  }


  // --- PROTECTED --
  protected AbstractFutureChain(Future<T> future) {
    this.future = future;
    this.future.setHandler(this::handler);
  }

  protected abstract Derived wrap(Future<T> result);

  private void addHandler(Handler<AsyncResult<T>> handler) {
    if (future.isComplete()) {
      // no point adding to the list, just pass the results on
      handler.handle(future);
    } else {
      handlers.add(handler);
    }
  }


  // ---- PRIVATE ----

  private void handler(AsyncResult<T> ar) {
    handlers.forEach(handler -> {
      try {
        handler.handle(ar);
      } catch (Throwable error) {
        LOG.error("failed to call handler", error);
      }
    });
  }

  private <R> R notImplemented() {
    throw new NotImplementedException();
  }

}
