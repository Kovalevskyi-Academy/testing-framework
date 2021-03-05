package academy.kovalevskyi.testing.service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;

/**
 * Allows to catch users console prints and print it while test is running.
 */
public class StdConsoleHandler extends Thread {

  private final ByteArrayOutputStream buffer;
  private final PrintStream defaultStdout;
  private boolean live;
  private volatile boolean newEntry;

  public StdConsoleHandler(final ByteArrayOutputStream buffer, final PrintStream defaultStdout) {
    this.buffer = buffer;
    this.defaultStdout = defaultStdout;
    this.setDaemon(true);
  }

  @Override
  public void run() {
    live = true;
    while (live) {
      if (buffer.size() > 0) {
        synchronized (buffer) {
          if (newEntry) {
            newEntry = false;
            defaultStdout.println(":");
          }
          defaultStdout.print(Ansi.ansi().fg(Color.MAGENTA).a(buffer.toString()).reset());
          buffer.reset();
        }
      }
    }
  }

  public void terminate() {
    live = false;
  }

  public boolean isNewEntry() {
    return newEntry;
  }

  public void newEntry() {
    synchronized (this) {
      newEntry = true;
    }
  }
}
