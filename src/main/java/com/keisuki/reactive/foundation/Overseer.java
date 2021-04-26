package com.keisuki.reactive.foundation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Overseer {
  private static final Logger LOGGER = LoggerFactory.getLogger(Overseer.class);

  public void startComponent(final Component component) {
    new Thread() {
      @Override
      public void run() {
        while (true) {
          try {
            component.run();
          } catch (final InterruptedException ex) {
            interrupt();
            return;
          } catch (final Exception ex) {
            LOGGER.error("Exception in " + component, ex);
          }
        }
      }
    }.start();
  }
}
