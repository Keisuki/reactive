package com.keisuki.reactive.routing;

import com.keisuki.reactive.foundation.Component;
import com.keisuki.reactive.foundation.MessageSink;
import com.keisuki.reactive.foundation.MessageSource;
import com.keisuki.reactive.http.HttpRequest;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestDispatcher implements Component {
  private final MessageSource<HttpRequest> requests;
  private final Collection<Route> routes;
  private final MessageSink<HttpRequest> unmatchedRequests;

  public RequestDispatcher(
      final MessageSource<HttpRequest> requests,
      final Collection<Route> routes,
      final MessageSink<HttpRequest> unmatchedRequests) {
    this.requests = requests;
    this.routes = routes;
    this.unmatchedRequests = unmatchedRequests;
  }

  @Override
  public void run() throws InterruptedException {
    final HttpRequest request = requests.next();

    for (final Route route : routes) {
      if (route.applyTo(request)) {
        return;
      }
    }

    unmatchedRequests.send(request);
  }

  public static class Route {
    private final Pattern pattern;
    private final MessageSink<HttpRequest> sink;

    public Route(
        final String regex,
        final MessageSink<HttpRequest> sink) {
      this(Pattern.compile(regex), sink);
    }

    public Route(
        final Pattern pattern,
        final MessageSink<HttpRequest> sink) {
      this.pattern = pattern;
      this.sink = sink;
    }

    private boolean applyTo(final HttpRequest request) {
      final Matcher matcher = pattern.matcher(request.getPath());
      if (!matcher.matches()) {
        return false;
      }

      sink.send(request);
      return true;
    }
  }
}
