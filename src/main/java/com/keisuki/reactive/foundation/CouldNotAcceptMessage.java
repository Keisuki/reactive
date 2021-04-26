package com.keisuki.reactive.foundation;

public class CouldNotAcceptMessage extends RuntimeException {
  private final Object messageObject;

  public CouldNotAcceptMessage(final Object messageObject) {
    this(messageObject, null);
  }

  public CouldNotAcceptMessage(final Object messageObject, final Throwable cause) {
    super("The sink could not accept the message " + messageObject, cause);
    this.messageObject = messageObject;
  }

  public Object getMessageObject() {
    return messageObject;
  }
}
