package com.keisuki.reactive;

import com.keisuki.reactive.foundation.Component;
import com.keisuki.reactive.foundation.MessageSink;

public class Counter implements Component {
  private final MessageSink<? super Integer> sink;

  private int counter = 0;

  public Counter(final MessageSink<? super Integer> sink) {
    this.sink = sink;
  }


  @Override
  public void run() throws Exception {
    Thread.sleep(1000);
    sink.send(counter++);
  }
}
