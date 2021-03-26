package academy.kovalevskyi.testing.service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

/**
 * This class can be used for all tests that are going to intercept what will be displayed in the
 * method under test.
 */
public abstract class AbstractStdCaptor {

  private static final PrintStream DEFAULT_STDOUT = System.out;
  private static final PrintStream DEFAULT_STDERR = System.err;
  private static final ByteArrayOutputStream OUT_STREAM_BUFFER = new ByteArrayOutputStream();
  private static final ByteArrayOutputStream ERR_STREAM_BUFFER = new ByteArrayOutputStream();

  /**
   * Activates captor.
   */
  @BeforeAll
  static void setUpCustomOutput() {
    System.setOut(new PrintStream(OUT_STREAM_BUFFER));
    System.setErr(new PrintStream(ERR_STREAM_BUFFER));
  }

  /**
   * Deactivates captor.
   */
  @AfterAll
  static void setUpDefaultOutput() {
    System.out.close();
    System.err.close();
    System.setOut(DEFAULT_STDOUT);
    System.setErr(DEFAULT_STDERR);
  }

  /**
   * Resets all buffers.
   */
  @AfterEach
  final void resetBuffersData() {
    OUT_STREAM_BUFFER.reset();
    ERR_STREAM_BUFFER.reset();
  }

  /**
   * Provides all captured text printed into standard output stream.
   *
   * @return text from standard output stream
   */
  protected final String getStdOutContent() {
    return OUT_STREAM_BUFFER.toString();
  }

  /**
   * Provides all captured text printed into standard error stream.
   *
   * @return text from standard error stream
   */
  protected final String getStdErrContent() {
    return ERR_STREAM_BUFFER.toString();
  }
}