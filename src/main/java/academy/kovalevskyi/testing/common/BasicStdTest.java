package academy.kovalevskyi.testing.common;

import academy.kovalevskyi.testing.AbstractTestExecutor;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * This class is required for all tests that are going to intercept what
 * will be displayed in the method under test.
 * Test classes with display interceptor methods must inherit from this abstract class.
 */
public abstract class BasicStdTest extends AbstractTestExecutor {

  protected final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
  private PrintStream printStream;

  @BeforeEach
  public void setUpCustomOutput() {
    printStream = System.out;
    System.setOut(new PrintStream(outputStreamCaptor));
  }

  @AfterEach
  public void setUpDefaultOutput() {
    System.setOut(printStream);
  }
}