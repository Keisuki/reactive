package com.keisuki.reactive.http;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpResponse {
  private final UUID requestUuid;
  private final HttpStatus status;
  private final Map<String, String[]> headers;
  private final String body;

  private HttpResponse(
      final UUID requestUuid,
      final HttpStatus status,
      final Map<String, String[]> headers,
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

    final String headerLines = headers.entrySet()
        .stream()
        .map(entry -> Stream.of(entry.getValue())
            .map(value -> entry.getKey() + ": " + value)
            .collect(Collectors.joining("\r\n")))
        .collect(Collectors.joining("\r\n"));

    return statusLine
        + (headerLines.isEmpty() ? "" : "\r\n" + headerLines)
        + (body == null ? "" : "\r\n\r\n" + body);
  }

  public Builder toBuilder() {
    return newBuilder(requestUuid, status)
        .withHeaderArrays(headers)
        .withBody(body);
  }

  public static Builder newBuilder(final UUID uuid, final HttpStatus status) {
    return new Builder(uuid, status);
  }

  public static class Builder {
    private final UUID uuid;
    private final HttpStatus status;
    private final Map<String, List<String>> headers;
    private String body;

    private Builder(final UUID uuid, final HttpStatus status) {
      this.uuid = uuid;
      this.status = status;
      headers = new LinkedHashMap<>();
    }

    public Builder withHeader(final String key, final String value) {
      headers.computeIfAbsent(key, k -> new LinkedList<>()).add(value);
      return this;
    }

    public Builder withHeader(final String key, final String[] value) {
      headers.computeIfAbsent(key, k -> new LinkedList<>()).addAll(Arrays.asList(value));
      return this;
    }

    public Builder withHeaders(final Map<String, String> headers) {
      headers.forEach(this::withHeader);
      return this;
    }

    public Builder withHeaderArrays(final Map<String, String[]> headers) {
      headers.forEach(this::withHeader);
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
          headers.entrySet()
              .stream()
              .collect(Collectors.toUnmodifiableMap(
                  Entry::getKey,
                  entry -> entry.getValue().toArray(new String[0]))),
          body);
    }
  }
}
