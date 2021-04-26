package com.keisuki.reactive;

import com.keisuki.reactive.foundation.Component;
import com.keisuki.reactive.foundation.MessageSource;
import org.slf4j.Logger;

public class Printer implements Component {
  private final String name;
  private final MessageSource<?> queue;
  private final Logger logger;

  public Printer(final String name, final MessageSource<?> queue, final Logger logger) {
    this.name = name;
    this.queue = queue;
    this.logger = logger;
  }

  @Override
  public void run() throws InterruptedException {
    logger.info("{} <({})", name, queue.next());
  }
}
