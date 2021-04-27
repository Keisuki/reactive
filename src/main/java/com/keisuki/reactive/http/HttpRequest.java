package com.keisuki.reactive.http;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class HttpRequest {
  private static final String[] EMPTY_STRING_ARRAY = new String[0];

  private final UUID uuid;
  private final boolean invalidRequest;
  private final String method;
  private final String path;
  private final Map<String, String[]> headers;
  private final String body;

  private HttpRequest(
      final UUID uuid,
      boolean invalidRequest,
      final String method,
      final String path,
      final Map<String, String[]> headers,
      final String body) {
    this.uuid = uuid;
    this.invalidRequest = invalidRequest;
    this.method = method;
    this.path = path;
    this.headers = headers;
    this.body = body;
  }

  public UUID getUuid() {
    return uuid;
  }

  public boolean isInvalidRequest() {
    return invalidRequest;
  }

  public String getMethod() {
    return method;
  }

  public String getPath() {
    return path;
  }

  public Optional<String> getBody() {
    return Optional.ofNullable(body);
  }

  public String[] getHeaders(final String key) {
    return headers.getOrDefault(key, EMPTY_STRING_ARRAY);
  }

  public Optional<String> getHeader(final String key) {
    final String[] headers = getHeaders(key);

    return headers.length == 0 ? Optional.empty() : Optional.ofNullable(headers[0]);
  }

  public Builder toBuilder() {
    if (invalidRequest) {
      throw new UnsupportedOperationException("Cannot transform invalid requests");
    }

    return newBuilder(uuid)
        .withMethod(method)
        .withPath(path)
        .withHeaderArrays(headers)
        .withBody(body);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    HttpRequest request = (HttpRequest) o;
    return invalidRequest == request.invalidRequest
        && Objects.equals(uuid, request.uuid)
        && Objects.equals(method, request.method)
        && Objects.equals(path, request.path)
        && Objects.equals(headers, request.headers)
        && Objects.equals(body, request.body);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uuid, invalidRequest, method, path, headers, body);
  }

  @Override
  public String toString() {
    return "HttpRequest{" +
        "uuid=" + uuid +
        ", invalidRequest=" + invalidRequest +
        ", method='" + method + '\'' +
        ", path='" + path + '\'' +
        ", headers=" + headers +
        ", body='" + body + '\'' +
        '}';
  }

  public static HttpRequest invalidRequest() {
    return new HttpRequest(
        UUID.randomUUID(),
        true,
        null,
        null,
        Map.of(),
        null);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static Builder newBuilder(final UUID uuid) {
    return new Builder(uuid);
  }

  static HttpRequest parseFrom(final String data) {
    final String[] lines = data.split("\r\n");
    if (lines.length == 0) {
      return invalidRequest();
    }

    final String[] requestLine = lines[0].split(" ");

    if (requestLine.length != 3) {
      return invalidRequest();
    }

    final Builder builder = newBuilder()
        .withMethod(requestLine[0])
        .withPath(requestLine[1]);

    int currentLine = 1;
    for (; currentLine < lines.length; currentLine++) {
      final String line = lines[currentLine];
      if (line.isEmpty()) {
        break; // We've hit the end of the headers
      }

      final String[] parts = line.split(": ", 2);
      if (parts.length != 2) {
        return invalidRequest();
      }

      builder.withHeader(parts[0], parts[1]);
    }

    if (currentLine < lines.length - 1) {
      // We have a body coming up
      final String body = String.join(
          "\n",
          Arrays.copyOfRange(lines, currentLine + 1, lines.length));

      builder.withBody(body);
    }

    return builder.build();
  }

  public static class Builder {
    private final UUID uuid;
    private String method;
    private String path;
    private Map<String, List<String>> headers;
    private String body;

    public Builder() {
      this(UUID.randomUUID());
    }

    public Builder(final UUID uuid) {
      this.uuid = uuid;
      headers = new LinkedHashMap<>();
    }

    public Builder withMethod(final String method) {
      this.method = method;
      return this;
    }

    public Builder withPath(final String path) {
      this.path = path;
      return this;
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

    public HttpRequest build() {
      return new HttpRequest(
          uuid,
          false,
          method,
          path,
          headers.entrySet()
              .stream()
              .collect(Collectors.toUnmodifiableMap(
                  Entry::getKey,
                  entry -> entry.getValue().toArray(new String[0]))),
          body);
    }
  }
}
