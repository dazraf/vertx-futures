package io.dazraf.vertx.futures;

import io.dazraf.vertx.futures.tuple.Tuple2;
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

abstract class FutureChainImpl<T, Derived extends FutureChainImpl<T, Derived>>
  implements FutureChain<T, Derived>, Handler<AsyncResult<T>> {

  private static final Logger LOG = getLogger(FutureChainImpl.class);

  private boolean failed;
  private boolean succeeded;
  private T result;
  private Throwable cause;

  private final List<Handler<AsyncResult<T>>> handlers = new CopyOnWriteArrayList<>();

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

  @Override
  public <R> FutureChain1<R> then(Function<T, Future<R>> tFutureFunction) {
    FutureChain1<R> result = new FutureChain1<>();
    setHandler(ar -> {
      if (ar.failed()) {
        result.fail(ar.cause());
        return;
      }
      // succcess
      try {
        final Future<R> newFuture = tFutureFunction.apply(ar.result());
        newFuture.setHandler(result);
      } catch (Throwable throwable) {
        result.fail(throwable);
      }
    });
    return result;
  }

  @Override
  public <T1, T2> FutureChain2<T1, T2> then2(Function<T, Future<Tuple2<T1, T2>>> tFutureFunction) {
    FutureChain2<T1, T2> result = new FutureChain2<>();
    setHandler(ar -> {
      if (ar.failed()) {
        result.fail(ar.cause());
        return;
      }
      // succcess
      try {
        final Future<Tuple2<T1, T2>> newFuture = tFutureFunction.apply(ar.result());
        newFuture.setHandler(result);
      } catch (Throwable throwable) {
        result.fail(throwable);
      }
    });
    return result;
  }


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
          LOG.trace("peekSuccess consumer failed", throwable);
        }
      }
    });
    return getThis();
  }

  @Override
  public Derived onError(Consumer<Throwable> consumer) {
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
  public Derived onResult(Consumer<AsyncResult<T>> consumer) {
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
}
