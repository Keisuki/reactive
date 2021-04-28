package com.keisuki.reactive.http;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {
  @Test
  @DisplayName("A HttpRequest parsed from invalid data has invalidRequest=true")
  void testInvalidRequest() {
    final HttpRequest request = HttpRequest.parseFrom("Invaliddata");

    assertThat(request.isInvalidRequest(), is(true));
  }

  @Test
  @DisplayName("A HttpRequest can be parsed from a HTTP payload")
  void testParse() {
    final HttpRequest request = HttpRequest.parseFrom(""
        + "POST /test HTTP/1.1\r\n"
        + "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n"
        + "Host: localhost\r\n"
        + "Content-Type: text/plain\r\n"
        + "\r\n"
        + "Request body");

    assertThat(request.getMethod(), is("POST"));
    assertThat(request.getPath(), is("/test"));
    assertThat(
        request.getHeaders().get("User-Agent").get(),
        is("Mozilla/4.0 (compatible; MSIE5.01; Windows NT)"));
    assertThat(request.getHeaders().get("Host").get(), is("localhost"));
    assertThat(request.getHeaders().get("Content-Type").get(), is("text/plain"));
    assertThat(request.getBody().get(), is("Request body"));
  }

  @Test
  @DisplayName("Query parameters are parsed correctly")
  void testParseQueryParameters() {
    final HttpRequest request = HttpRequest.parseFrom(""
        + "POST /search?filter=one&filter=two&sort=price HTTP/1.1\r\n"
        + "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n"
        + "Host: localhost\r\n"
        + "Content-Type: text/plain\r\n"
        + "\r\n"
        + "Request body");

    assertThat(request.getQueryParameters(), is(Parameters.newBuilder()
        .withValue("filter", List.of("one", "two"))
        .withValue("sort", "price")
        .build()));
  }

  @Test
  @DisplayName("Query parameters are not included in the path")
  void testParseQueryParametersNotIncludedInPath() {
    final HttpRequest request = HttpRequest.parseFrom(""
        + "POST /search?filter=one&filter=2&sort=price HTTP/1.1\r\n"
        + "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\r\n"
        + "Host: localhost\r\n"
        + "Content-Type: text/plain\r\n"
        + "\r\n"
        + "Request body");

    assertThat(request.getPath(), is("/search"));
  }
}