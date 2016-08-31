package io.dazraf.vertx.futures;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import io.dazraf.vertx.futures.processors.FutureProcessor;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.impl.NoStackTraceThrowable;

/**
 * Implementation of Futures
 */
class FuturesImpl<T> implements Futures<T> {

  private T result;
  private Throwable cause;
  private boolean succeeded;
  private boolean failed;

  private final Future parent; // required to avoid the parent being GC'd

  private List<Consumer<Future<T>>> dependents = new LinkedList<>();

  @SuppressWarnings("unchecked")
  public static final <T> Futures<T> when(Future<T> future) {
    if (future instanceof Futures) {
      // if already a Futures perfectly good to reuse and will minimise the
      return (Futures<T>) future;
    } else {
      return new FuturesImpl<>(future, future);
    }
  }

  public FuturesImpl(Future parent) {
    this.parent = parent;
  }

  public FuturesImpl(Future<T> future, Future parent) {
    this(parent);
    initialise(future);
  }

  private FuturesImpl<T> initialise(Future<T> future) {
    future.setHandler(asyncResult -> {
      if (asyncResult.failed()) {
        fail(asyncResult.cause());
      } else if (asyncResult.succeeded()) {
        complete(asyncResult.result());
      }
      notifyAllDependents(this);
    });
    return this;
  }

  private void notifyAllDependents(Future<T> future) {
    dependents.forEach(dependent -> dependent.accept(future));
  }

  @Override
  public <R> Futures<R> then(FutureProcessor<T, R> processor) {
    FuturesImpl<R> result = new FuturesImpl<>(this);
    addHandler(future -> result.initialise(processor.apply(future)));
    return result;
  }

  @Override
  public boolean isComplete() {
    return succeeded || failed;
  }

  @Override
  public Future<T> setHandler(Handler<AsyncResult<T>> handler) {
    addHandler(handler::handle);
    return this;
  }

  @Override
  public void complete(T result) {
    this.result = result;
    this.succeeded = true;
    this.failed = false;
    this.cause = null;
  }

  @Override
  public void complete() {
    complete(null);
  }

  @Override
  public void fail(Throwable throwable) {
    this.result = null;
    this.succeeded = false;
    this.failed = true;
    this.cause = throwable;
  }

  @Override
  public void fail(String failureMessage) {
    fail(new NoStackTraceThrowable(failureMessage));
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

  private void addHandler(Consumer<Future<T>> handler) {
    if (isComplete()) {
      handler.accept(this);
    } else {
      dependents.add(handler);
    }
  }
}
