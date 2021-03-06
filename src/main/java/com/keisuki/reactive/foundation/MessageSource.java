package com.keisuki.reactive.foundation;

/**
 * A blocking source of messages of type T, where T must be an immutable or effectively immutable
 * type
 */
public interface MessageSource<T> {
  /**
   * Receive the next message from this source.  If one is not readily available, this method blocks
   * until one becomes available.
   */
  T next() throws InterruptedException;

  /**
   * Receive the next message from this source, or null if one is not readily available
   */
  T nextNonBlocking();

  /**
   * Apply backpressure to any sinks affecting this source, telling them to stop sending more
   * messages
   */
  void stopMessages(boolean stop);
}
