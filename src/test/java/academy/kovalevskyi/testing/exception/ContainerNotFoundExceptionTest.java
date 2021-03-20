package academy.kovalevskyi.testing.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ContainerNotFoundExceptionTest {

  private static String message;
  private static ContainerNotFoundException exception;

  @BeforeAll
  public static void setUp() {
    message = "Some text";
    exception = new ContainerNotFoundException(message);
  }

  @Test
  public void testConstructor() {
    assertEquals(message, exception.getMessage());
  }

  @Test
  public void testInheritance() {
    assertTrue(exception instanceof FrameworkException);
  }
}