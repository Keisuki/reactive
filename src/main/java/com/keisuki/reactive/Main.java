package com.keisuki.reactive;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
  public static void main(final String[] args) {
    final BlockingQueue<Integer> integersToPrint = new LinkedBlockingQueue<>(10);

    final Overseer overseer = new Overseer();

    overseer.startComponent(new Counter(integersToPrint));
    overseer.startComponent(new Counter(integersToPrint));
    overseer.startComponent(new Printer("integers", integersToPrint));
  }
}
