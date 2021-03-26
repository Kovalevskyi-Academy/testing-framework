package academy.kovalevskyi.testing.test.five;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import academy.kovalevskyi.testing.annotation.Container;
import academy.kovalevskyi.testing.test.TestProvider;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@Container(course = TestProvider.class, week = 4, day = 0, id = 1)
public class TestClassEight {

  private final String message = "Some message";

  @Test
  public void simpleOne() {
    assumeTrue(false);
  }

  @Test
  public void simpleTwo() {
    assumeTrue(false, message);
  }

  @RepeatedTest(2)
  public void repeated() {
    assumeTrue(false, message);
  }

  @ParameterizedTest
  @NullSource
  public void parameterized(Object object) {
    assumeTrue(false, message);
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2})
  public void repeatedParameterized(int x) {
    assumeTrue(false, message);
  }

  @Test
  public void successful() {
    assertTrue(true);
  }
}
