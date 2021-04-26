package com.keisuki.reactive;

import com.keisuki.reactive.foundation.Component;
import com.keisuki.reactive.foundation.MessageSource;
import java.util.concurrent.BlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Printer implements Component {
  private static final Logger LOGGER = LoggerFactory.getLogger(Printer.class);

  private final String name;
  private final MessageSource<?> queue;

  public Printer(final String name, final MessageSource<?> queue) {
    this.name = name;
    this.queue = queue;
  }

  @Override
  public void run() throws InterruptedException {
    LOGGER.info("{} <({})", name, queue.next());
  }
}
