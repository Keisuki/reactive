package com.keisuki.reactive.foundation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MessageQueueTest {
  private MessageQueue<Object> queue;
  private ExecutorService executorService;

  @BeforeEach
  void setUp() {
    executorService = TestUtils.createExecutorService(1);
    queue = new MessageQueue<>(2);
  }

  @AfterEach
  void tearDown() {
    executorService.shutdownNow();
  }

  @Test
  @DisplayName("If a message queue has no messages, next() blocks")
  void testNextBlocks() throws InterruptedException {
    final boolean[] finished = new boolean[1];
    executorService.submit(() -> {
      try {
        queue.next();
        finished[0] = true;
      } catch (final InterruptedException ex) {
        Thread.currentThread().interrupt();
      }
    });

    Thread.sleep(500);
    assertThat(finished, is(new boolean[]{false}));
  }

  @Test
  @DisplayName("If a message queue has a message, next() returns it")
  void testNextReturnsMessage() throws InterruptedException {
    final Object message = new Object();
    queue.send(message);

    assertThat(queue.next(), is(message));
  }

  @Test
  @DisplayName("If a message queue receives a message while next() is blocking, next() returns the "
      + "message")
  void testNextReturnsMessageWhileBlocking() throws InterruptedException {
    final Object message = new Object();
    final Object[] result = new Object[1];
    executorService.submit(() -> {
      try {
        result[0] = queue.next();
      } catch (final InterruptedException ex) {
        Thread.currentThread().interrupt();
      }
    });

    queue.send(message);
    Thread.sleep(100);
    assertThat(result, is(new Object[]{message}));
  }

  @Test
  @DisplayName("If the queue is full, send(message) throws CouldNotAcceptMessage")
  void testFull() {
    queue.send(new Object());
    queue.send(new Object());

    assertThrows(CouldNotAcceptMessage.class, () -> queue.send(new Object()));
  }
}