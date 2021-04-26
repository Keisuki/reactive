package com.keisuki.reactive;

import java.util.concurrent.BlockingQueue;

public class Counter implements Component {
  private final BlockingQueue<? super Integer> queue;

  private int counter = 0;

  public Counter(final BlockingQueue<? super Integer> queue) {
    this.queue = queue;
  }


  @Override
  public void run() throws Exception {
    Thread.sleep(1000);
    queue.add(counter++);
  }
}
