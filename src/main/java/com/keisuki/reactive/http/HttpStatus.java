package com.keisuki.reactive.http;

public enum HttpStatus {
  OK(200, "OK"),
  NO_CONTENT(204, "No Content"),
  BAD_REQUEST(400, "Bad Request"),
  NOT_FOUND(404, "Not Found"),
  INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
  SERVICE_UNAVAILABLE(503, "Service Unavailable");

  int statusCode;
  String message;

  HttpStatus(final int statusCode, final String message) {
    this.statusCode = statusCode;
    this.message = message;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public String getMessage() {
    return message;
  }
}
