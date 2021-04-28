package com.keisuki.reactive.routing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.keisuki.reactive.foundation.MessageQueue;
import com.keisuki.reactive.foundation.MessageSink;
import com.keisuki.reactive.http.HttpRequest;
import com.keisuki.reactive.routing.RequestDispatcher.Route;
import com.keisuki.reactive.util.Eventually;
import com.keisuki.reactive.util.TestUtils;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestDispatcherTest {
  private ExecutorService executorService;

  @BeforeEach
  void setUp() {
    executorService = TestUtils.createExecutorService(1);
  }

  @AfterEach
  void tearDown() {
    executorService.shutdownNow();
  }

  @Test
  @DisplayName("If no HttpRequests are received, none are sent")
  void testIdle() {
    final MessageSink<HttpRequest> sink = mock(MessageSink.class);

    TestUtils.startComponent(executorService, new RequestDispatcher(
        new MessageQueue<>(1),
        List.of(new Route("/test", sink)),
        sink));

    Eventually.assertAlways(2, ChronoUnit.SECONDS, () -> verifyZeroInteractions(sink));
  }

  @Test
  @DisplayName("HttpRequests are dispatched to the sink for the matching route")
  void testDispatch() {
    final MessageSink<HttpRequest> homeSink = mock(MessageSink.class);
    final MessageSink<HttpRequest> loginSink = mock(MessageSink.class);
    final MessageSink<HttpRequest> unmatchedSink = mock(MessageSink.class);

    final MessageQueue<HttpRequest> input = new MessageQueue<>(10);

    TestUtils.startComponent(executorService, new RequestDispatcher(
        input,
        List.of(
            new Route("^/$", homeSink),
            new Route("^/login/\\d+$", loginSink)),
        unmatchedSink));

    final HttpRequest homeRequest = HttpRequest.newBuilder()
        .withMethod("GET")
        .withPath("/")
        .build();

    final HttpRequest loginRequest = HttpRequest.newBuilder()
        .withMethod("POST")
        .withPath("/login/123")
        .build();

    input.send(homeRequest);
    input.send(loginRequest);

    Eventually.assertEventually(2, ChronoUnit.SECONDS, () -> {
      verify(homeSink, times(1)).send(homeRequest);
      verifyNoMoreInteractions(homeSink);

      verify(loginSink, times(1)).send(loginRequest);
      verifyNoMoreInteractions(loginSink);

      verifyZeroInteractions(unmatchedSink);
    });
  }

  @Test
  @DisplayName("Unmatched HttpRequests are dispatched to the unmatched sink")
  void testDispatchUnmatched() {
    final MessageSink<HttpRequest> homeSink = mock(MessageSink.class);
    final MessageSink<HttpRequest> unmatchedSink = mock(MessageSink.class);

    final MessageQueue<HttpRequest> input = new MessageQueue<>(10);

    TestUtils.startComponent(executorService, new RequestDispatcher(
        input,
        List.of(
            new Route("^/$", homeSink)),
        unmatchedSink));

    final HttpRequest unmatchedRequest = HttpRequest.newBuilder()
        .withMethod("GET")
        .withPath("/zofwnmfdka")
        .build();

    input.send(unmatchedRequest);

    Eventually.assertEventually(2, ChronoUnit.SECONDS, () -> {
      verify(unmatchedSink, times(1)).send(unmatchedRequest);
      verifyNoMoreInteractions(unmatchedSink);

      verifyZeroInteractions(homeSink);
    });
  }
}