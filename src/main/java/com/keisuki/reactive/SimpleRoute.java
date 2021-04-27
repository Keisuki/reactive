package com.keisuki.reactive;

import com.keisuki.reactive.foundation.Component;
import com.keisuki.reactive.foundation.MessageSink;
import com.keisuki.reactive.foundation.MessageSource;
import com.keisuki.reactive.http.HttpRequest;
import com.keisuki.reactive.http.HttpResponse;
import com.keisuki.reactive.http.HttpStatus;

public class SimpleRoute implements Component {
  private final MessageSource<HttpRequest> requests;
  private final MessageSink<HttpResponse> responses;

  public SimpleRoute(
      final MessageSource<HttpRequest> requests,
      final MessageSink<HttpResponse> responses) {
    this.requests = requests;
    this.responses = responses;
  }

  @Override
  public void run() throws Exception {
    final HttpRequest request = requests.next();
    responses.send(HttpResponse.newBuilder(request.getUuid(), HttpStatus.OK)
        .withHeader("X-Custom-Header", "Value")
        .withBody("Response body")
        .build());
  }
}
