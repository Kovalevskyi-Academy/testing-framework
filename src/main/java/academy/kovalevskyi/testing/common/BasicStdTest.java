package academy.kovalevskyi.testing.common;

import academy.kovalevskyi.testing.AbstractTestExecutor;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class BasicStdTest extends AbstractTestExecutor {

  protected final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
  private PrintStream printStream;

  @BeforeEach
  public void init() {
    printStream = System.out;
    System.setOut(new PrintStream(outputStreamCaptor));
  }

  @AfterEach
  public void destroy() {
    System.setOut(printStream);
  }
}