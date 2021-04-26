package com.keisuki.reactive.foundation;

/**
 * A non-blocking sink of messages of type T, where T must be an immutable or effectively immutable
 * type
 */
public interface MessageSink<T> {
  void send(final T message);
}
