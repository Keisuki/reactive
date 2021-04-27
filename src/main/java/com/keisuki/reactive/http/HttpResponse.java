package com.keisuki.reactive.http;

import java.util.Objects;
import java.util.UUID;

public class HttpResponse {
  private final UUID requestUuid;
  private final HttpStatus status;

  public HttpResponse(final UUID requestUuid, final HttpStatus status) {
    this.requestUuid = requestUuid;
    this.status = status;
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
    HttpResponse that = (HttpResponse) o;
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
    return "HTTP/1.1 " + status.getStatusCode() + " " + status.getMessage() + "\r\n\r\ndata here";
  }
}
