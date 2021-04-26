package com.keisuki.reactive.foundation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestUtils {
  private TestUtils(){};

  public static ExecutorService createExecutorService(final int size) {
    return new ThreadPoolExecutor(1, size, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<>());
  }

  public static void startComponent(
      final ExecutorService executorService,
      final Component component) {
    executorService.submit(() -> {
      while (true) {
        component.run();
      }
    });
  }
}
