package io.dazraf.vertx.futures.http;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class HttpFutures {
  public static HttpClientRequestWithFutureResponse future(HttpClientRequest request) {
    return new HttpClientRequestWithFutureResponse(request);
  }

  public static Future<Buffer> body(HttpClientResponse response) {
    Future<Buffer> result = Future.future();
    response.bodyHandler(result::complete);
    return result;
  }

  public static Future<JsonObject> bodyObject(HttpClientResponse response) {
    Future<JsonObject> result = Future.future();
    response.bodyHandler(buffer -> {
      try {
        JsonObject jo = buffer.toJsonObject();
        result.complete(jo);
      } catch (Throwable err) {
        result.fail(err);
      }
    });
    return result;
  }

  public static Future<JsonArray> bodyArray(HttpClientResponse response) {
    Future<JsonArray> result = Future.future();
    response.bodyHandler(buffer -> {
      try {
        JsonArray ja = buffer.toJsonArray();
        result.complete(ja);
      } catch (Throwable err) {
        result.fail(err);
      }
    });
    return result;
  }

  public static void checkHttpSuccess(HttpClientResponse response) {
    int category = response.statusCode() / 100;
    if (category == 4 || category == 5) {
      throw new RuntimeException("Http request failed with " + response.statusCode() + " : " + response.statusMessage());
    }
  }

}
