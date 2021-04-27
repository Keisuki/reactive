package com.keisuki.reactive.util;

import java.time.Instant;
import java.time.temporal.TemporalUnit;

public class Eventually {
  private Eventually() {
  }

  public static void assertAlways(
      final long timeout,
      final TemporalUnit unit,
      final EventuallyCondition condition) {
    final Instant cutOffTime = Instant.now().plus(timeout, unit);

    while (Instant.now().isBefore(cutOffTime)) {
      try {
        condition.assertMet();
      } catch (final Throwable ex) {
        throw new AssertionError("Condition not met", ex);
      }
    }
  }

  public static void assertEventually(
      final long timeout,
      final TemporalUnit unit,
      final EventuallyCondition condition) {
    final Throwable ex = checkConditionUntilTimeout(timeout, unit, condition);
    if (ex != null) {
      throw new AssertionError("Condition not met", ex);
    }
  }

  public static void assertNever(
      final long timeout,
      final TemporalUnit unit,
      final EventuallyCondition condition) {
    final Throwable ex = checkConditionUntilTimeout(timeout, unit, condition);
    if (ex == null) {
      throw new AssertionError("Condition met");
    }
  }

  private static Throwable checkConditionUntilTimeout(
      final long timeout,
      final TemporalUnit unit,
      final EventuallyCondition condition) {
    final Instant cutOffTime = Instant.now().plus(timeout, unit);
    Throwable lastFailure = null;

    while (Instant.now().isBefore(cutOffTime)) {
      try {
        condition.assertMet();
        return null;
      } catch (final Throwable ex) {
        lastFailure = ex;
      }
    }

    return lastFailure;
  }

  @FunctionalInterface
  public interface EventuallyCondition {
    void assertMet() throws Exception;
  }
}
