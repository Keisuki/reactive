package com.keisuki.reactive.http;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpResponse {
  private final UUID requestUuid;
  private final HttpStatus status;
  private final Parameters headers;
  private final String body;

  private HttpResponse(
      final UUID requestUuid,
      final HttpStatus status,
      final Parameters headers,
      final String body) {
    this.requestUuid = requestUuid;
    this.status = status;
    this.headers = headers;
    this.body = body;
  }

  public UUID getRequestUuid() {
    return requestUuid;
  }

  public HttpStatus getStatus() {
    return status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final HttpResponse that = (HttpResponse) o;
    return status == that.status && Objects.equals(requestUuid, that.requestUuid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(requestUuid, status);
  }

  @Override
  public String toString() {
    return "HttpResponse{" +
        "requestUuid=" + requestUuid +
        ", status=" + status +
        '}';
  }

  String toData() {
    final String statusLine = "HTTP/1.1 " + status.getStatusCode() + " " + status.getMessage();

    final String headerLines = headers.getParameters().entrySet()
        .stream()
        .map(entry -> entry.getValue()
            .stream()
            .map(value -> entry.getKey() + ": " + value)
            .collect(Collectors.joining("\r\n")))
        .collect(Collectors.joining("\r\n"));

    return statusLine
        + (headerLines.isEmpty() ? "" : "\r\n" + headerLines)
        + (body == null ? "" : "\r\n\r\n" + body);
  }

  public Builder toBuilder() {
    return newBuilder(requestUuid, status)
        .withHeaders(headers)
        .withBody(body);
  }

  public static Builder newBuilder(final UUID uuid, final HttpStatus status) {
    return new Builder(uuid, status);
  }

  public static class Builder {
    private final UUID uuid;
    private final HttpStatus status;
    private Parameters.Builder headers;
    private String body;

    private Builder(final UUID uuid, final HttpStatus status) {
      this.uuid = uuid;
      this.status = status;
      headers = Parameters.newBuilder();
    }

    public Builder withHeader(final String key, final String value) {
      headers.withValue(key, value);
      return this;
    }

    public Builder withHeader(final String key, final List<String> value) {
      headers.withValue(key, value);
      return this;
    }

    public Builder withHeaders(final Map<String, String> headers) {
      this.headers.withValues(headers);
      return this;
    }

    public Builder withHeaders(final Parameters headers) {
      this.headers = headers.toBuilder();
      return this;
    }

    public Builder withBody(final String body) {
      this.body = body;
      return this;
    }

    public HttpResponse build() {
      return new HttpResponse(
          uuid,
          status,
          headers.build(),
          body);
    }
  }
}
