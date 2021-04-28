package com.keisuki.reactive;

import com.keisuki.reactive.foundation.MessageQueue;
import com.keisuki.reactive.foundation.Overseer;
import com.keisuki.reactive.http.HttpRequest;
import com.keisuki.reactive.http.HttpResponse;
import com.keisuki.reactive.http.HttpServer;
import com.keisuki.reactive.http.HttpStatus;
import com.keisuki.reactive.routing.RequestDispatcher;
import com.keisuki.reactive.routing.RequestDispatcher.Route;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.List;
import java.util.regex.Pattern;

public class Main {
  public static void main(final String[] args) throws IOException {
    final MessageQueue<HttpRequest> requests = new MessageQueue<>(10);
    final MessageQueue<HttpResponse> responses = new MessageQueue<>(10);

    final MessageQueue<HttpRequest> unmatchedRequests = new MessageQueue<>(10);
    final MessageQueue<HttpRequest> rootRequests = new MessageQueue<>(10);

    final Overseer overseer = new Overseer();

    final AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open();
    server.bind(new InetSocketAddress(8080));

    overseer.startComponent(new HttpServer(server, requests, responses));
    overseer.startComponent(new RequestDispatcher(
        requests,
        List.of(new Route(Pattern.compile("^/$"), rootRequests)),
        unmatchedRequests));
    overseer.startComponent(new SimpleRoute(
        rootRequests, responses, HttpStatus.OK, "Welcome to Keisuki's reactive http server"));
    overseer.startComponent(new SimpleRoute(
        unmatchedRequests, responses, HttpStatus.NOT_FOUND, "Not found"));
  }
}
