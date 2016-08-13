package io.dazraf.vertx.futures;

import io.dazraf.vertx.futures.tuple.Tuple2;
import io.dazraf.vertx.futures.tuple.Tuple3;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.impl.NoStackTraceThrowable;
import org.slf4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.slf4j.LoggerFactory.*;

public abstract class FutureChainImpl<T, Derived extends FutureChainImpl<T, Derived>>
  implements FutureChain<T, Derived>, Handler<AsyncResult<T>> {

  private static final Logger LOG = getLogger(FutureChainImpl.class);
  final Object parent; // used to ensure parent is not GC'd

  private boolean failed;
  private boolean succeeded;
  private T result;
  private Throwable cause;

  private final List<Handler<AsyncResult<T>>> handlers = new CopyOnWriteArrayList<>();

  FutureChainImpl(Object parent) {
    this.parent = parent;
  }

  // ---- Future Implementation ----

  @Override
  public boolean isComplete() {
    return succeeded || failed;
  }

  @Override
  public Future<T> setHandler(Handler<AsyncResult<T>> handler) {
    addHandler(handler);
    return this;
  }


  @Override
  public void complete(T result) {
    checkNotCompleted();
    this.result = result;
    this.succeeded = true;
    callHandlers();
  }


  @Override
  public void complete() {
    checkNotCompleted();
    this.result = null;
    this.succeeded = true;
    callHandlers();
  }

  @Override
  public void fail(Throwable throwable) {
    checkNotCompleted();
    this.cause = throwable == null ? new NoStackTraceThrowable("no throwable") : throwable;
    this.failed = true;
    callHandlers();
  }

  @Override
  public void fail(String failureMessage) {
    checkNotCompleted();
    this.cause = failureMessage == null ? new NoStackTraceThrowable("no message") : new RuntimeException(failureMessage);
    this.failed = true;
    notImplemented();
  }

  @Override
  public T result() {
    return result;
  }

  @Override
  public Throwable cause() {
    return cause;
  }

  @Override
  public boolean succeeded() {
    return succeeded;
  }

  @Override
  public boolean failed() {
    return failed;
  }

  // --- PUBLIC ---

  @Override
  public void handle(AsyncResult<T> event) {
    if (event.succeeded()) {
      this.complete(event.result());
    } else {
      this.fail(event.cause());
    }
  }

  // --- then() functions

  @Override
  public <R> FutureChain1<R> then(Function<T, Future<R>> thenFn) {
    FutureChain1<R> result = createFuture1();
    thenX(result, thenFn);
    return result;
  }

  @Override
  public <T1, T2> FutureChain2<T1, T2> then2(Function<T, Future<Tuple2<T1, T2>>> thenFn) {
    FutureChain2<T1, T2> result = createFuture2();
    thenX(result, thenFn);
    return result;

  }

  public <T1, T2, T3> FutureChain3<T1, T2, T3> then3(Function<T, Future<Tuple3<T1, T2, T3>>> thenFn) {
    FutureChain3<T1, T2, T3> result = createFuture3();
    thenX(result, thenFn);
    return result;
  }

  // -- method to return another future, if failed

  @Override
  public FutureChain1<T> ifFailed(Function<Throwable, Future<T>> ifFailedFn) {
    FutureChain1<T> result = createFuture1();
    ifFailedX(result, ifFailedFn);
    return result;
  }


  // --- methods to receive state

  @Override
  public Derived onSuccess(Consumer<T> consumer) {
    Derived next = create();

    setHandler(ar -> {
      if (ar.succeeded()) {
        try {
          consumer.accept(ar.result());
          next.complete(ar.result());
        } catch (Throwable throwable) {
          next.fail(throwable);
        }
      } else {
        next.fail(ar.cause());
      }
    });
    return next;
  }

  @Override
  public Derived peekSuccess(Consumer<T> consumer) {
    setHandler(ar -> {
      if (ar.succeeded()) {
        try {
          consumer.accept(ar.result());
        } catch (Throwable throwable) {
          LOG.trace("peek success threw an exception that I swallowed by design.", throwable);
        }
      }
    });
    return getThis();
  }

  @Override
  public Derived peekFail(Consumer<Throwable> consumer) {
    setHandler(ar -> {
      if (ar.failed()) {
        try {
          consumer.accept(ar.cause());
        } catch (Throwable throwable) {
          LOG.trace("peekFail function threw an exception that I swallowed by design", throwable);
        }
      }
    });
    return getThis();
  }

  @Override
  public Derived peekComplete(Consumer<AsyncResult<T>> consumer) {
    setHandler(consumer::accept);
    return getThis();
  }

  @Override
  public Derived onFail(Consumer<Throwable> consumer) {
    Derived result = create();
    setHandler(ar -> {
      if (failed) {
        try {
          consumer.accept(ar.cause());
          result.fail(ar.cause());
        } catch (Throwable throwable) {
          result.fail(throwable);
        }
      } else {
        result.complete(ar.result());
      }
    });
    return result;
  }

  @Override
  public Derived onComplete(Consumer<AsyncResult<T>> consumer) {
    Derived result = create();
    setHandler(ar -> {
      try {
        consumer.accept(ar);
        result.completer().handle(ar);
      } catch (Throwable throwable) {
        result.fail(throwable);
      }
    });
    return result;
  }

  @Override
  public FutureChain1<Void> mapVoid() {
    FutureChain1<Void> result = createFuture1();
    addHandler(ar -> {
      if (ar.failed()) {
        result.fail(ar.cause());
      } else {
        result.complete();
      }
    });
    return result;
  }

  @Override
  public <R> FutureChain1<R> map(Function<T, R> mapFn) {
    FutureChain1<R> result = createFuture1();
    setHandler(ar -> {
      if (ar.succeeded()) {
        try {
          result.complete(mapFn.apply(ar.result()));
        } catch(Throwable throwable) {
          result.fail(throwable);
        }
      } else {
        result.fail(ar.cause());
      }
    });
    return result;
  }


  @Override
  public <R> FutureChain1<R> map(R value) {
    FutureChain1<R> result = createFuture1();
    setHandler(ar -> {
      if (ar.succeeded()) {
        try {
          result.complete(value);
        } catch(Throwable throwable) {
          result.fail(throwable);
        }
      } else {
        result.fail(ar.cause());
      }
    });
    return result;
  }

  // --- PROTECTED --

  /**
   * Create a new instance of the Derived type
   * @return this
   */
  protected abstract Derived create();

  @SuppressWarnings("unchecked")
  protected Derived getThis() {
    return (Derived)this;
  }

  private <R> void thenX(Future<R> collector, Function<T, Future<R>> thenFn) {
    setHandler(ar -> {
      if (ar.failed()) {
        collector.fail((ar.cause()));
        return;
      }
      // success
      try {
        thenFn.apply(ar.result()).setHandler(collector.completer());
      } catch (Throwable err) {
        collector.fail(err);
      }
    });
  }

  void ifFailedX(Future<T> collector, Function<Throwable, Future<T>> ifFailedFn) {
    setHandler(ar -> {
      if (ar.failed()) {
        try {
          ifFailedFn.apply(ar.cause()).setHandler(collector.completer());
        } catch (Throwable throwable) {
          collector.fail(throwable);
        }
      } else {
        collector.complete(ar.result());
      }
    });
  }

  // ---- PRIVATE ----

  private void addHandler(Handler<AsyncResult<T>> handler) {
    if (isComplete()) {
      // no point adding to the list, just pass the results on
      handler.handle(this);
    } else {
      handlers.add(handler);
    }
  }


  private void callHandlers() {
    if (isComplete()) {
      handlers.forEach(handler -> {
        try {
          handler.handle(this);
        } catch (Throwable error) {
          LOG.error("failed to call handler", error);
        }
      });
    }
  }


  private <R> R notImplemented() {
    throw new NotImplementedException();
  }

  private void checkNotCompleted() {
    if (isComplete()) {
      throw new RuntimeException("Future is already complete");
    }
  }


  private <T1> FutureChain1<T1> createFuture1() {
    return new FutureChain1<>((Object)this);
  }

  private <T1, T2> FutureChain2<T1, T2> createFuture2() {
    return new FutureChain2<>(this);
  }

  private <T1, T2, T3> FutureChain3<T1, T2, T3> createFuture3() {
    return new FutureChain3<>(this);
  }


}
