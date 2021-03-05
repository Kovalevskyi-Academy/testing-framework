package academy.kovalevskyi.testing.service;

import org.fusesource.jansi.Ansi.Color;

/**
 * Set of states of a test.
 */
public enum State {

  SUCCESSFUL("OK", Color.GREEN),
  FAILED("BAD", Color.RED),
  ABORTED("NONE", Color.YELLOW),
  DISABLED("OFF", Color.CYAN),
  RUNNING("RUN", Color.BLUE),
  INTERRUPTED("FATAL", Color.RED),
  NO_METHOD("ERROR", Color.RED),
  NO_CLASS("ERROR", Color.RED);

  public final String status;
  public final Color color;

  State(String status, Color color) {
    this.status = status;
    this.color = color;
  }
}
