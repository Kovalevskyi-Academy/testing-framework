package academy.kovalevskyi.testing.model;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * This class is required for all tests that are going to intercept what will be displayed in the
 * method under test. Test containers with display interceptor methods must inherit from this
 * class.
 */
public abstract class AbstractStdoutContainer extends AbstractContainer {

  protected final ByteArrayOutputStream outputStreamCaptor;
  protected final ByteArrayOutputStream errorStreamCaptor;
  private final PrintStream defaultStdout;
  private final PrintStream defaultStderr;

  public AbstractStdoutContainer() {
    outputStreamCaptor = new ByteArrayOutputStream();
    errorStreamCaptor = new ByteArrayOutputStream();
    defaultStdout = System.out;
    defaultStderr = System.err;
  }

  @BeforeEach
  public void setUpCustomOutput() {
    System.setOut(new PrintStream(outputStreamCaptor));
    System.setErr(new PrintStream(errorStreamCaptor));
  }

  @AfterEach
  public void setUpDefaultOutput() {
    outputStreamCaptor.reset();
    errorStreamCaptor.reset();
    System.out.close();
    System.err.close();
    System.setOut(defaultStdout);
    System.setErr(defaultStderr);
  }
}