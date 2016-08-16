package io.dazraf.vertx.futures.http;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import javax.xml.ws.http.HTTPException;

/**
 * A set of factory functions to present Vert.x HTTP API as {@link Future}.
 */
public class HttpFutures {

  /**
   * Wrap a {@link HttpClientRequest} as a {@link Future Future&lt;HttpClientResponse&gt;}
   * The returned object has all the capabilities and methods of a {@link HttpClientRequest}.
   *<br><br>
   * This means that we can use this function like this to chain http operations:
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
   *
   * @param request the HttpClientRequest as created by methods such as {@link io.vertx.core.http.HttpClient#get(String)}
   * @return An object that implements both {@link Future Future&lt;HttpClientResponse&gt;} and implements all methods of {@link HttpClientRequest}
   */
  public static HttpClientRequestWithFutureResponse future(HttpClientRequest request) {
    return new HttpClientRequestWithFutureResponse(request);
  }

  /**
   * Given a {@link HttpClientResponse} returns a {@link Future Future&lt;Buffer&gt;} for retrieving the body of the request as a {@link Buffer}
   * @param response the response from a http call
   * @return A future that will resolve to a {@link Buffer} if the Http response is not a HTTP error status
   */
  public static Future<Buffer> body(HttpClientResponse response) {
    Future<Buffer> result = Future.future();
    response.bodyHandler(result::complete);
    return result;
  }

  /**
   * Similar to {@link #body} except that it also parses the body buffer to a {@link JsonObject}
   * @param response the response from a http call
   * @return A {@link Future} that will resolve to a {@link JsonObject} if no HTTP errors or exceptions
   */
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

  /**
   * Similar to {@link #body} except that it also parses the body buffer to a {@link JsonArray}
   * @param response the response from a http call
   * @return A {@link Future} that will resolve to a {@link JsonArray} if no HTTP errors or exceptions
   */
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

  /**
   * A helper function that checks the response code for a client or server error state 4xx, 5xx
   * @param response the response from a http request
   * @throws HTTPException if 4xx or 5xx
   */
  public static void checkHttpSuccess(HttpClientResponse response) throws HTTPException {
    int category = response.statusCode() / 100;
    if (category == 4 || category == 5) {
      throw new HTTPException(response.statusCode()) {
        @Override
        public String toString() {
          return "HTTP request failed with status code " + getStatusCode();
        }
      };
    }
  }
}
