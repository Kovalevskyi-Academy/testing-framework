package academy.kovalevskyi.testing.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FrameworkExceptionTest {

  private static String message;
  private static FrameworkException exception;

  @BeforeAll
  public static void setUp() {
    message = "Some text";
    exception = new FrameworkException(message) {
    };
  }

  @Test
  public void testAbstractClass() {
    assertTrue(Modifier.isAbstract(FrameworkException.class.getModifiers()));
  }

  @Test
  public void testConstructor() {
    assertEquals(message, exception.getMessage());
  }

  @Test
  public void testInheritance() {
    assertTrue(exception instanceof RuntimeException);
  }
}