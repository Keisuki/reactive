package com.keisuki.reactive;

import com.keisuki.reactive.foundation.MessageQueue;
import com.keisuki.reactive.foundation.Overseer;
import com.keisuki.reactive.http.HttpRequest;
import com.keisuki.reactive.http.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import org.slf4j.LoggerFactory;

public class Main {
  public static void main(final String[] args) throws IOException {
    final MessageQueue<HttpRequest> requests = new MessageQueue<>(10);

    final Overseer overseer = new Overseer();

    final AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
    server.bind(new InetSocketAddress(8080));

    overseer.startComponent(new HttpServer(server, requests));
    overseer.startComponent(new Printer(
        "requests",
        requests,
        LoggerFactory.getLogger(Main.class)));
  }
}
