package io.dazraf.vertx.futures.http;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpConnection;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpVersion;

/**
 *
 * Instances of this class:
 * <ol>
 *   <li>wrap an existing {@link HttpClientRequest}</li>
 *   <li>present all methods specified by {@link HttpClientRequest}</li>
 *   <li>implement {@link Future Future&lt;HttpClientResponse&gt;} </li>
 * </ol>
 * <br>
 * This means that one can use this class to chain http operations.
 * <br><br>
 * e.g.
 * <pre>
 * {@code
 * when(future(httpClient.get("/")).end())
 * .onSuccess(HttpFutures::checkHttpSuccess)
 * .then(response -> bodyObject(response))
 * .onSuccess(body -> assertThat(context, body.containsKey("time"), is(true)))
 * .onFail((Runnable) context::fail);
 * }
 * </pre>
 */
public class HttpClientRequestWithFutureResponse implements Future<HttpClientResponse>
{
  private final HttpClientRequest request;
  private final Future<HttpClientResponse> future = Future.future();

  HttpClientRequestWithFutureResponse(HttpClientRequest request) {
    this.request = request;
    this.request.handler(this::handleResponse);
    this.request.exceptionHandler(this::handleException);
  }

  private void handleException(Throwable throwable) {
    future.fail(throwable);
  }

  private void handleResponse(HttpClientResponse httpClientResponse) {
    future.complete(httpClientResponse);
  }

  @Override
  public boolean isComplete() {
    return future.isComplete();
  }

  @Override
  public HttpClientRequestWithFutureResponse setHandler(Handler<AsyncResult<HttpClientResponse>> handler) {
    future.setHandler(ar -> {
      handler.handle(this);
    });
    return this;
  }

  @Override
  public void complete(HttpClientResponse result) {
    throw new UnsupportedOperationException(); // because only the http callback can complete this
  }

  @Override
  public void complete() {
    throw new UnsupportedOperationException(); // because only the http callback can complete this
  }

  @Override
  public void fail(Throwable throwable) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void fail(String failureMessage) {
    throw new UnsupportedOperationException();
  }

  @Override
  public HttpClientResponse result() {
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

  public HttpClientRequestWithFutureResponse exceptionHandler(Handler<Throwable> handler) {
    throw new UnsupportedOperationException();
  }

  public HttpClientRequestWithFutureResponse write(Buffer data) {request.write(data);
    return this;
  }

  public HttpClientRequestWithFutureResponse setWriteQueueMaxSize(int maxSize) {
    request.setWriteQueueMaxSize(maxSize);
    return this;
  }

  public boolean writeQueueFull() {
    return request.writeQueueFull();
  }

  public HttpClientRequestWithFutureResponse drainHandler(Handler<Void> handler) {
    request.drainHandler(handler);
    return this;
  }

  public HttpClientRequestWithFutureResponse handler(Handler<HttpClientResponse> handler) {
    throw new UnsupportedOperationException();
  }

  public HttpClientRequestWithFutureResponse pause() {
    request.pause();
    return this;
  }

  public HttpClientRequestWithFutureResponse resume() {
    request.resume();
    return this;
  }

  public HttpClientRequestWithFutureResponse endHandler(Handler<Void> endHandler) {
    request.endHandler(endHandler);
    return this;
  }

  public HttpClientRequestWithFutureResponse setChunked(boolean chunked) {
    request.setChunked(chunked);
    return this;
  }

  public boolean isChunked() {
    return request.isChunked();
  }

  public HttpMethod method() {
    return request.method();
  }

  public String getRawMethod() {
    return request.getRawMethod();
  }

  public HttpClientRequestWithFutureResponse setRawMethod(String method) {
    request.setRawMethod(method);
    return this;
  }

  public String uri() {
    return request.uri();
  }

  public String path() {
    return request.path();
  }

  public String query() {
    return request.query();
  }

  public HttpClientRequestWithFutureResponse setHost(String host) {
    request.setHost(host);
    return this;
  }

  public String getHost() {
    return request.getHost();
  }

  public MultiMap headers() {
    return request.headers();
  }

  public HttpClientRequestWithFutureResponse putHeader(String name, String value) {
    request.putHeader(name, value);
    return this;
  }

  public HttpClientRequestWithFutureResponse putHeader(CharSequence name, CharSequence value) {
    request.putHeader(name, value);
    return this;
  }

  public HttpClientRequestWithFutureResponse putHeader(String name, Iterable<String> values) {
    request.putHeader(name, values);
    return this;
  }

  public HttpClientRequestWithFutureResponse putHeader(CharSequence name, Iterable<CharSequence> values) {
    request.putHeader(name, values);
    return this;
  }

  public HttpClientRequestWithFutureResponse write(String chunk) {
    request.write(chunk);
    return this;
  }

  public HttpClientRequestWithFutureResponse write(String chunk, String enc) {
    request.write(chunk, enc);
    return this;
  }

  public HttpClientRequestWithFutureResponse continueHandler(Handler<Void> handler) {
    request.continueHandler(handler);
    return this;
  }

  public HttpClientRequestWithFutureResponse sendHead() {
    request.sendHead();
    return this;
  }

  public HttpClientRequestWithFutureResponse sendHead(Handler<HttpVersion> completionHandler) {
    request.sendHead(completionHandler);
    return this;
  }

  public HttpClientRequestWithFutureResponse end(String chunk) {
    request.end(chunk);
    return this;
  }

  public HttpClientRequestWithFutureResponse end(String chunk, String enc) {
    request.end(chunk, enc);
    return this;
  }

  public HttpClientRequestWithFutureResponse end(Buffer chunk) {
    request.end(chunk);
    return this;
  }

  public HttpClientRequestWithFutureResponse end() {
    request.end();
    return this;
  }

  public HttpClientRequestWithFutureResponse setTimeout(long timeoutMs) {
    request.setTimeout(timeoutMs);
    return this;
  }

  public HttpClientRequestWithFutureResponse pushHandler(Handler<HttpClientRequest> handler) {
    request.pushHandler(handler);
    return this;
  }

  public HttpClientRequestWithFutureResponse reset(long code) {
    request.reset(code);
    return this;
  }

  public HttpConnection connection() {
    return request.connection();
  }

  public HttpClientRequestWithFutureResponse connectionHandler(Handler<HttpConnection> handler) {
    request.connectionHandler(handler);
    return this;
  }

  public HttpClientRequestWithFutureResponse writeCustomFrame(int type, int flags, Buffer payload) {
    request.writeCustomFrame(type, flags, payload);
    return this;
  }
}
