package academy.kovalevskyi.testing.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class NotAnnotatedContainerExceptionTest {

  private static String message;
  private static NotAnnotatedContainerException exception;

  @BeforeAll
  public static void setUp() {
    message = "Some text";
    exception = new NotAnnotatedContainerException(message);
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