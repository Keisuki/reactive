package com.keisuki.reactive.http;

public enum HttpStatus {
  OK(200, "OK");

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
