package com.keisuki.reactive.http;

import com.keisuki.reactive.utils.IOUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * A wrapper around AsynchronousSocketChannel which handles http request/response
 *
 * I know this isn't a robust, secure way of handling and parsing data.  In a real, production
 * project, a well-developed and proven library or framework would (of course) be necessary.
 */
class HttpConnection {
  private final AsynchronousSocketChannel channel;
  private volatile HttpRequest request = null;

  HttpConnection(final AsynchronousSocketChannel channel) {
    this.channel = channel;
    final ByteBuffer buffer = ByteBuffer.allocate(1024);
    channel.read(buffer, buffer, IOUtils.completionHandler(
        this::handleData,
        ((throwable, byteBuffer) -> {})));
  }

  HttpRequest getRequest() {
    return request;
  }

  void sendResponseAndCloseConnection(final HttpResponse response) {
    channel.write(ByteBuffer.wrap(
        response.toData().getBytes(StandardCharsets.UTF_8)),
        null,
        IOUtils.completionHandler(this::closeConnection, this::closeConnection));
  }

  private void handleData(final int count, final ByteBuffer buffer) {
    final String data = new String(buffer.array(), StandardCharsets.UTF_8);
    request = HttpRequest.parseFrom(data);
  }

  private <A, T> void closeConnection(final T value, final A attachment) {
    try {
      channel.close();
    } catch (final IOException ex) {
      throw new RuntimeException(ex);
    }
  }
}
