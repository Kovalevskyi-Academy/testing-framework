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
  private PrintStream printStream;

  private AbstractStdoutContainer() {
    outputStreamCaptor = new ByteArrayOutputStream();
  }

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