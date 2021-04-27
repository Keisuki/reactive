package com.keisuki.reactive.util;

import com.keisuki.reactive.foundation.CouldNotAcceptMessage;
import com.keisuki.reactive.foundation.MessageSink;
import java.util.function.Consumer;

public class TestSink<T> implements MessageSink<T> {
  private final Consumer<T> consumer;

  private volatile boolean acceptMessages = true;

  public TestSink(final Consumer<T> consumer) {
    this.consumer = consumer;
  }

  @Override
  public void send(final T message) throws CouldNotAcceptMessage {
    consumer.accept(message);
  }

  @Override
  public boolean canAcceptMessages() {
    return acceptMessages;
  }

  public void acceptMessages(final boolean value) {
    this.acceptMessages = value;
  }
}
