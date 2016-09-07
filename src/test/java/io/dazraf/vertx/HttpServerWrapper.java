package io.dazraf.vertx;

import org.slf4j.Logger;

import java.util.function.Consumer;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;

import static io.dazraf.vertx.SocketTestUtils.getFreePort;
import static io.dazraf.vertx.futures.Futures.when;
import static io.vertx.core.Future.future;
import static org.slf4j.LoggerFactory.getLogger;

public class HttpServerWrapper {
  private static final String MESSAGE_OK = "OK";
  private static final Logger LOG = getLogger(HttpServerWrapper.class);

  private final int serverPort;
  private final HttpServer httpServer;

  private HttpServerWrapper(int serverPort, HttpServer httpServer) {
    this.serverPort = serverPort;
    this.httpServer = httpServer;
  }

  public static Future<HttpServerWrapper> createHttpServer(Vertx vertx) {
    return createHttpServer(vertx, request -> request.bodyHandler(buffer -> {
      request.response().end(MESSAGE_OK);
    }));
  }

  public static Future<HttpServerWrapper> createHttpServer(Vertx vertx, Consumer<HttpServerRequest> handler) {
    Future<HttpServerWrapper> httpServer = future();
    int serverPort = getFreePort();
    LOG.info("initialising server");
    vertx.createHttpServer()
        .requestHandler(handler::accept)
        .listen(serverPort, ar -> {
          if (ar.failed()) {
            httpServer.fail(ar.cause());
          } else {
            httpServer.complete(new HttpServerWrapper(serverPort, ar.result()));
          }
        });
    // this means that we can chain multiple parallel flows on this future
    // something that we can't do with a straight Future
    return when(httpServer);
  }

  public Future<Void> close() {
    Future<Void> result = future();
    httpServer.close(result.completer());
    return result;
  }

  public int port() {
    return serverPort;
  }
}
