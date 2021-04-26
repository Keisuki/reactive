package com.keisuki.reactive.foundation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import com.keisuki.reactive.Counter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CounterTest {
  private ExecutorService executorService;

  @BeforeEach
  void setUp() {
    executorService = TestUtils.createExecutorService(1);
  }

  @AfterEach
  void tearDown() {
    executorService.shutdownNow();
  }

  @Test
  @DisplayName("The counter component counts up at a rate of 1 per second")
  void testCounter() throws InterruptedException {
    final List<Integer> values = new LinkedList<>();
    final Counter counter = new Counter(values::add);

    TestUtils.startComponent(executorService, counter);
    Thread.sleep(500);
    assertThat(values, is(List.of()));
    Thread.sleep(1000);
    assertThat(values, is(List.of(0)));
    Thread.sleep(1000);
    assertThat(values, is(List.of(0, 1)));
    Thread.sleep(1000);
    assertThat(values, is(List.of(0, 1, 2)));
    Thread.sleep(1000);
    assertThat(values, is(List.of(0, 1, 2, 3)));
  }
}
