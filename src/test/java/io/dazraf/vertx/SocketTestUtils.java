package io.dazraf.vertx;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SocketTestUtils {

  public static int getFreePort() {
    return getFreePort(1).get(0);
  }

  public static List<Integer> getFreePort(int numberOfPorts) {
    try {
      List<ServerSocket> sockets = new ArrayList<>();
      for (int i = 0; i < numberOfPorts; i++) {
        sockets.add(new ServerSocket(0));
      }
      sockets.stream().forEach(SocketTestUtils::close);
      return sockets.stream()
          .map(ServerSocket::getLocalPort)
          .collect(Collectors.toList());
    } catch (IOException e) {
      throw new RuntimeException("Failed to allocate socket ports", e);
    }
  }

  private static void close(ServerSocket serverSocket) {
    try {
      serverSocket.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
