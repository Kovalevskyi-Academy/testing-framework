package academy.kovalevskyi.testing.annotation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CourseProviderTest {

  @Test
  public void testTypeIsInterface() {
    assertTrue(CourseProvider.class.isInterface());
  }
}