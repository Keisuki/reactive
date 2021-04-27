package com.keisuki.reactive.http;

import com.keisuki.reactive.foundation.Component;
import com.keisuki.reactive.foundation.MessageSink;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Future;

public class HttpServer implements Component {
  private final AsynchronousServerSocketChannel server;
  private final MessageSink<? super HttpRequest> sink;
  private final Set<HttpConnection> clientsWaitingForRequest = new LinkedHashSet<>();
  private Future<AsynchronousSocketChannel> clientFuture;


  public HttpServer(
      final AsynchronousServerSocketChannel server,
      final MessageSink<? super HttpRequest> sink) {
    this.sink = sink;
    this.server = server;
    clientFuture = server.accept();
  }

  @Override
  public void run() throws Exception {
    if (clientFuture.isDone()) {
      clientsWaitingForRequest.add(new HttpConnection(clientFuture.get()));
      clientFuture = server.accept();
    }

    for (final HttpConnection client : clientsWaitingForRequest) {
      final HttpRequest request = client.getRequest();
      if (request != null) {
        clientsWaitingForRequest.remove(client);
        sink.send(request);
      }
    }
  }
}
