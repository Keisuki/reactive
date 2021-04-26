package com.keisuki.reactive.foundation;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueue<T> implements MessageSource<T>, MessageSink<T> {
  protected final BlockingQueue<T> queue;

  public MessageQueue(final int capacity) {
    queue = new LinkedBlockingQueue<>(capacity);
  }

  @Override
  public void send(T message) {
    try {
      queue.add(message);
    } catch (final IllegalStateException ex) {
      throw new CouldNotAcceptMessage(message, ex);
    }
  }

  @Override
  public T next() throws InterruptedException {
    return queue.take();
  }
}
