package com.keisuki.reactive.http;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;

import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {
  @Test
  @DisplayName("A HttpResponse can be serialised for transmission")
  void testSerialize() {
    final HttpResponse response = HttpResponse.newBuilder(UUID.randomUUID(), HttpStatus.OK)
        .withHeader("Content-Type", "text/plain")
        .withHeader("Set-Cookie", "authtoken=abcd; Expires=Wed, 21 Oct 2021 05:28:00 GMT")
        .withHeader("Set-Cookie", "userid=123")
        .withBody("You are now logged in")
        .build();

    final String data = response.toData();

    final String[] lines = data.split("\r\n");

    assertThat(lines[0], is("HTTP/1.1 200 OK"));
    assertThat(Arrays.asList(Arrays.copyOfRange(lines, 1, 4)), containsInAnyOrder(
        "Content-Type: text/plain",
        "Set-Cookie: authtoken=abcd; Expires=Wed, 21 Oct 2021 05:28:00 GMT",
        "Set-Cookie: userid=123"));
    assertThat(lines[4], is(""));
    assertThat(lines[5], is("You are now logged in"));
    assertThat(lines.length, is(6));
  }
}