package io.dazraf.vertx.futures.http;

import io.vertx.core.http.HttpClientResponse;

public class HttpException extends RuntimeException {

  private final HttpClientResponse response;

  HttpException(HttpClientResponse response, String message) {
    super(message);
    this.response = response;
  }

  public HttpClientResponse getResponse() {
    return response;
  }
}
