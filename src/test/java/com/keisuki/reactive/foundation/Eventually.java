package com.keisuki.reactive.foundation;

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
      } catch (final Exception ex) {
        throw new AssertionError("Condition not met", ex);
      }
    }
  }

  public static void assertEventually(
      final long timeout,
      final TemporalUnit unit,
      final EventuallyCondition condition) {
    final Exception ex = checkConditionUntilTimeout(timeout, unit, condition);
    if (ex != null) {
      throw new AssertionError("Condition not met", ex);
    }
  }

  public static void assertNever(
      final long timeout,
      final TemporalUnit unit,
      final EventuallyCondition condition) {
    final Exception ex = checkConditionUntilTimeout(timeout, unit, condition);
    if (ex == null) {
      throw new AssertionError("Condition met");
    }
  }

  private static Exception checkConditionUntilTimeout(
      final long timeout,
      final TemporalUnit unit,
      final EventuallyCondition condition) {
    final Instant cutOffTime = Instant.now().plus(timeout, unit);
    Exception lastFailure = null;

    while (Instant.now().isBefore(cutOffTime)) {
      try {
        condition.assertMet();
        return null;
      } catch (final Exception ex) {
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
