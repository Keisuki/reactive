package com.keisuki.reactive.http;

import com.keisuki.reactive.foundation.Component;
import com.keisuki.reactive.foundation.MessageSink;
import com.keisuki.reactive.foundation.MessageSource;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HttpServer implements Component {
  private final AsynchronousServerSocketChannel server;
  private final MessageSink<? super HttpRequest> requestSink;
  private final MessageSource<? extends HttpResponse> responseSource;
  private final Set<HttpConnection> clientsWaitingForRequest = new LinkedHashSet<>();
  private final Map<UUID, HttpConnection> clientsWaitingForResponse = new HashMap<>();
  private Future<AsynchronousSocketChannel> clientFuture;

  public HttpServer(
      final AsynchronousServerSocketChannel server,
      final MessageSink<? super HttpRequest> requestSink,
      final MessageSource<? extends HttpResponse> responseSource) {
    this.requestSink = requestSink;
    this.responseSource = responseSource;
    this.server = server;
    clientFuture = server.accept();
  }

  @Override
  public void run() throws Exception {
    acceptNewConnections();
    receiveRequestData();
    sendResponses();
  }

  private void sendResponses() {
    HttpResponse response;
    while ((response = responseSource.nextNonBlocking()) != null) {
      final UUID uuid = response.getRequestUuid();
      final HttpConnection client = clientsWaitingForResponse.get(uuid);
      clientsWaitingForResponse.remove(uuid);
      client.sendResponseAndCloseConnection(response);
    }
  }

  private void receiveRequestData() {
    for (final HttpConnection client : clientsWaitingForRequest) {
      final HttpRequest request = client.getRequest();
      if (request != null) {
        clientsWaitingForRequest.remove(client);
        clientsWaitingForResponse.put(request.getUuid(), client);
        requestSink.send(request);
      }
    }
  }

  private void acceptNewConnections() throws InterruptedException, ExecutionException {
    while (clientFuture.isDone()) {
      clientsWaitingForRequest.add(new HttpConnection(clientFuture.get()));
      clientFuture = server.accept();
    }
  }
}
