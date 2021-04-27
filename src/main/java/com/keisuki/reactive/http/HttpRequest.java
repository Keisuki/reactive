package com.keisuki.reactive.http;

import java.util.Objects;
import java.util.UUID;

public class HttpRequest {
  private final UUID uuid;
  private final String method;
  private final String path;

  public HttpRequest(final String method, final String path) {
    uuid = UUID.randomUUID();
    this.method = method;
    this.path = path;
  }

  public UUID getUuid() {
    return uuid;
  }

  public String getMethod() {
    return method;
  }

  public String getPath() {
    return path;
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
    return Objects.equals(method, request.method)
        && Objects.equals(path, request.path);
  }

  @Override
  public int hashCode() {
    return Objects.hash(method, path);
  }

  @Override
  public String toString() {
    return "HttpRequest{" +
        "method='" + method + '\'' +
        ", path='" + path + '\'' +
        '}';
  }
}
