package io.dazraf.vertx.futures.processors;

public class WrappedException extends RuntimeException {
  public WrappedException(Throwable cause) {
    super(cause);
  }
}
