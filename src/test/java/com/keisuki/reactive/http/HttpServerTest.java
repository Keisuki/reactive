package com.keisuki.reactive.http;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;

import com.keisuki.reactive.util.Eventually;
import com.keisuki.reactive.util.TestSink;
import com.keisuki.reactive.util.TestUtils;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpServerTest {
  private ExecutorService executorService;
  private List<HttpRequest> receivedRequests;
  private OkHttpClient http;
  private AsynchronousServerSocketChannel serverChannel;

  @BeforeEach
  void setUp() throws IOException {
    executorService = TestUtils.createExecutorService(1);
    receivedRequests = new LinkedList<>();
    http = new OkHttpClient();
    serverChannel = AsynchronousServerSocketChannel.open();
    serverChannel.bind(new InetSocketAddress(8080));
    TestUtils.startComponent(executorService, new HttpServer(
        serverChannel,
        new TestSink<>(receivedRequests::add)));
  }

  @AfterEach
  void tearDown() throws IOException {
    executorService.shutdownNow();
    serverChannel.close();
  }

  @Test
  @DisplayName("If no http requests are received, no messages are sent to the sink")
  void testIdle() {
    Eventually.assertAlways(
        2,
        ChronoUnit.SECONDS,
        () -> assertThat(receivedRequests, is(List.of())));
  }

  @Test
  @DisplayName("If a http request is received, a message is sent to the sink")
  void testReceive() {
    http.newCall(new Request.Builder()
        .get()
        .url("http://localhost:8080")
        .build()).enqueue(okHttpCallback(new CompletableFuture<>()));

    Eventually.assertEventually(
        3,
        ChronoUnit.SECONDS,
        () -> assertThat(receivedRequests.size(), is(1)));
  }

  @Test
  @DisplayName("Received messages are parsed correctly")
  void testParseMethodAndPath() throws InterruptedException {
    http.newCall(new Request.Builder()
        .get()
        .url("http://localhost:8080")
        .build()).enqueue(okHttpCallback(new CompletableFuture<>()));

    Thread.sleep(100);

    http.newCall(new Builder()
        .post(RequestBody.create("data".getBytes(StandardCharsets.UTF_8)))
        .url("http://localhost:8080/submitData")
        .build()).enqueue(okHttpCallback(new CompletableFuture<>()));

    Thread.sleep(100);

    http.newCall(new Builder()
        .delete()
        .url("http://localhost:8080/data/123")
        .build()).enqueue(okHttpCallback(new CompletableFuture<>()));

    Eventually.assertEventually(
        8,
        ChronoUnit.SECONDS,
        () -> assertThat(receivedRequests, containsInAnyOrder(
            new HttpRequest("GET", "/"),
            new HttpRequest("POST", "/submitData"),
            new HttpRequest("DELETE", "/data/123"))));
  }

  private Callback okHttpCallback(final CompletableFuture<Response> future) {
    return new Callback() {
      @Override
      public void onFailure(@NotNull final Call call, @NotNull final IOException e) {
        future.completeExceptionally(e);
      }

      @Override
      public void onResponse(@NotNull final Call call, @NotNull final Response response) {
        future.complete(response);
      }
    };
  }
}