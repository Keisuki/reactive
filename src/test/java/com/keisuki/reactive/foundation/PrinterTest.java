package com.keisuki.reactive.foundation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.keisuki.reactive.Printer;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class PrinterTest {
  private MessageSink<Object> sink;
  private ExecutorService executorService;
  private Logger logger;

  @BeforeEach
  void setUp() {
    executorService = TestUtils.createExecutorService(1);
    logger = mock(Logger.class);

    final MessageQueue<Object> queue = new MessageQueue<>(10);
    final Printer printer = new Printer("printer", queue, logger);
    TestUtils.startComponent(executorService, printer);

    sink = queue;
  }

  @AfterEach
  void tearDown() {
    executorService.shutdownNow();
  }

  @Test
  @DisplayName("If no messages are sent to the printer, nothing is printed")
  void testIdle() {
    Eventually.assertAlways(2, ChronoUnit.SECONDS, () -> verifyZeroInteractions(logger));
  }

  @Test
  @DisplayName("Each message sent to the printer is logged")
  void testPrint() {
    sink.send("test");
    Eventually.assertEventually(
        2,
        ChronoUnit.SECONDS,
        () -> verify(logger).info("{} <({})", "printer", "test"));
  }
}
