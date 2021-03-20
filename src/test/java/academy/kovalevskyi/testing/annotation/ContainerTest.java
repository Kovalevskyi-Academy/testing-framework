package academy.kovalevskyi.testing.annotation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ContainerTest {

  @Test
  public void testTypeIsAnnotation() {
    assertTrue(Container.class.isAnnotation());
  }
}
