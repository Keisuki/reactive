package com.keisuki.reactive.foundation;

/**
 * A blocking source of messages of type T, where T must be an immutable or effectively immutable
 * type
 */
public interface MessageSource<T> {
  T next() throws InterruptedException;
}
