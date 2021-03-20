package academy.kovalevskyi.testing.service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * This class can be used for all tests that are going to intercept what will be displayed in the
 * method under test.
 */
public abstract class AbstractStdCaptor {

  private ByteArrayOutputStream outputStreamCaptor;
  private ByteArrayOutputStream errorStreamCaptor;
  private final PrintStream defaultStdout;
  private final PrintStream defaultStderr;

  {
    defaultStdout = System.out;
    defaultStderr = System.err;
  }

  /**
   * Activates captor.
   */
  @BeforeEach
  void setUpCustomOutput() {
    outputStreamCaptor = new ByteArrayOutputStream();
    errorStreamCaptor = new ByteArrayOutputStream();
    System.setOut(new PrintStream(outputStreamCaptor));
    System.setErr(new PrintStream(errorStreamCaptor));
  }

  /**
   * Deactivates captor.
   */
  @AfterEach
  void setUpDefaultOutput() {
    System.out.close();
    System.err.close();
    System.setOut(defaultStdout);
    System.setErr(defaultStderr);
  }

  /**
   * Provides all captured text printed into standard output stream.
   *
   * @return text from standard output stream
   */
  public final String getStdOutText() {
    return outputStreamCaptor.toString();
  }

  /**
   * Provides all captured text printed into standard error stream.
   *
   * @return text from standard error stream
   */
  public final String getStdErrText() {
    return errorStreamCaptor.toString();
  }
}