package com.keisuki.reactive.foundation;

@FunctionalInterface
public interface Component {
  /**
   * The run method is repeatedly called in a loop in a single thread.  Each component needn't
   * be thread-safe except at the boundaries (such as queues), as it will only ever be run in
   * a single thread.
   */
  void run() throws Exception;
}
