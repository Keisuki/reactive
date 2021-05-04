package com.keisuki.reactive.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class HttpRequest {
  private static final Gson GSON = new Gson();

  private final UUID uuid;
  private final boolean invalidRequest;
  private final String method;
  private final String path;
  private final Parameters headers;
  private final Parameters queryParameters;
  private final String body;

  private HttpRequest(
      final UUID uuid,
      boolean invalidRequest,
      final String method,
      final String path,
      final Parameters headers,
      final Parameters queryParameters,
      final String body) {
    this.uuid = uuid;
    this.invalidRequest = invalidRequest;
    this.method = method;
    this.path = path;
    this.headers = headers;
    this.queryParameters = queryParameters;
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

  public Parameters getHeaders() {
    return headers;
  }

  public Parameters getQueryParameters() {
    return queryParameters;
  }

  public <T> Optional<T> getBodyAsJson(final Class<T> clazz) {
    return getBody().map(body -> GSON.fromJson(body, clazz));
  }

  public <T> Optional<T> getBodyAsJson(final TypeToken<T> type) {
    return getBody().map(body -> GSON.fromJson(body, type.getType()));
  }

  public Builder toBuilder() {
    if (invalidRequest) {
      throw new UnsupportedOperationException("Cannot transform invalid requests");
    }

    return newBuilder(uuid)
        .withMethod(method)
        .withPath(path)
        .withHeaders(headers)
        .withQueryParameters(queryParameters)
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
        Parameters.empty(),
        Parameters.empty(),
        null);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static Builder newBuilder(final UUID uuid) {
    return new Builder(uuid);
  }

  /**
   * This way of parsing HTTP requests is massively insecure.  This is just to grow my own
   * understanding, not to be used in a production environment.
   */
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
        .withMethod(requestLine[0]);

    parsePath(requestLine[1], builder);

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

  private static void parsePath(final String path, final Builder builder) {
    final String[] pathAndQueryString = path.split("\\?", 2);

    if (pathAndQueryString.length != 2) {
      builder.withPath(path);
      return;
    }

    builder.withPath(pathAndQueryString[0]);
    Stream.of(pathAndQueryString[1].split("&"))
        .map(part -> part.split("=", 2))
        .filter(part -> part.length == 2)
        .forEach(part -> builder.withQueryParameter(part[0], part[1]));
  }

  public static class Builder {
    private final UUID uuid;
    private String method;
    private String path;
    private Parameters.Builder headers;
    private Parameters.Builder queryParameters;
    private String body;

    public Builder() {
      this(UUID.randomUUID());
    }

    public Builder(final UUID uuid) {
      this.uuid = uuid;
      headers = Parameters.newBuilder();
      queryParameters = Parameters.newBuilder();
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

    public Builder withQueryParameter(final String key, final String value) {
      queryParameters.withValue(key, value);
      return this;
    }

    public Builder withQueryParameters(final Parameters parameters) {
      this.queryParameters = parameters.toBuilder();
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
          headers.build(),
          queryParameters.build(),
          body);
    }
  }
}
