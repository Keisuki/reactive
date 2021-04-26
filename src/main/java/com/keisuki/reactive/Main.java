package com.keisuki.reactive;

import com.keisuki.reactive.foundation.MessageQueue;
import com.keisuki.reactive.foundation.Overseer;
import org.slf4j.LoggerFactory;

public class Main {
  public static void main(final String[] args) {
    final MessageQueue<Integer> integersToPrint = new MessageQueue<>(10);

    final Overseer overseer = new Overseer();

    overseer.startComponent(new Counter(integersToPrint));
    overseer.startComponent(new Counter(integersToPrint));
    overseer.startComponent(new Printer(
        "integers",
        integersToPrint,
        LoggerFactory.getLogger(Main.class)));
  }
}
