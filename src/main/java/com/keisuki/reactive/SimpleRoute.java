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
  private final HttpStatus status;
  private final String response;

  public SimpleRoute(
      final MessageSource<HttpRequest> requests,
      final MessageSink<HttpResponse> responses,
      HttpStatus status, final String response) {
    this.requests = requests;
    this.responses = responses;
    this.status = status;
    this.response = response;
  }

  @Override
  public void run() throws Exception {
    final HttpRequest request = requests.next();
    responses.send(HttpResponse.newBuilder(request.getUuid(), status)
        .withBody(response)
        .build());
  }
}
