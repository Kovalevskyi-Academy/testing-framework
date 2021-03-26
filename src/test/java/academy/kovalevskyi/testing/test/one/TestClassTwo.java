package academy.kovalevskyi.testing.test.one;

import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.kovalevskyi.testing.annotation.Container;
import academy.kovalevskyi.testing.test.TestProvider;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@Container(course = TestProvider.class, week = 1, day = 2, id = 1)
public class TestClassTwo {

  private final String message = "Some message";

  @Test
  public void simple() {
    assertTrue(false, message);
  }

  @RepeatedTest(2)
  public void repeated() {
    assertTrue(false, message);
  }

  @ParameterizedTest
  @NullSource
  public void parameterized(Object object) {
    assertTrue(false, message);
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2})
  public void repeatedParameterized(int x) {
    assertTrue(false, message);
  }

  @Test
  public void successful() {
    assertTrue(true);
  }
}
