package com.keisuki.reactive.foundation;

/**
 * A non-blocking sink of messages of type T, where T must be an immutable or effectively immutable
 * type
 */
public interface MessageSink<T> {
  /**
   * Send the given message to this sink if possible.
   */
  void send(final T message) throws CouldNotAcceptMessage;

  /**
   * Check for backpressure from any downstream components.
   */
  boolean canAcceptMessages();
}
