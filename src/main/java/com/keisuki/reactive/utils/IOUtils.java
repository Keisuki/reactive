package com.keisuki.reactive.utils;

import java.nio.channels.CompletionHandler;
import java.util.function.BiConsumer;

public class IOUtils {
  private IOUtils() {}

  public static <V, A>CompletionHandler<V, A> completionHandler(
      final BiConsumer<V, A> onSuccess,
      final BiConsumer<Throwable, A> onFailure) {
    return new CompletionHandler<V, A>() {
      @Override
      public void completed(final V result, final A attachment) {
        onSuccess.accept(result, attachment);
      }

      @Override
      public void failed(final Throwable exc, final A attachment) {
        onFailure.accept(exc, attachment);
      }
    };
  }
}
