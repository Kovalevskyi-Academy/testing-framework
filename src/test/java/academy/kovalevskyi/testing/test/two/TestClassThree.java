package academy.kovalevskyi.testing.test.two;

import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.kovalevskyi.testing.annotation.Container;
import academy.kovalevskyi.testing.test.TestProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("SomeNameOfTestClass")
@Container(course = TestProvider.class, week = 1, day = 3, id = 0)
public class TestClassThree {

  private final String message = "Some message";

  @Test
  @DisplayName("SomeNameOfTestMethod1")
  public void simple() {
    assertTrue(true);
  }

  @RepeatedTest(2)
  @DisplayName("SomeNameOfTestMethod2")
  public void repeated() {
    assertTrue(true);
  }

  @ParameterizedTest
  @NullSource
  @DisplayName("SomeNameOfTestMethod3")
  public void parameterized(Object object) {
    assertTrue(true);
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2})
  @DisplayName("SomeNameOfTestMethod4(int x)")
  public void repeatedParameterized(int x) {
    assertTrue(true);
  }
}
