package academy.kovalevskyi.testing.test.three;

import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.kovalevskyi.testing.annotation.Container;
import academy.kovalevskyi.testing.test.TestProvider;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Container(course = TestProvider.class, week = 2, day = 2, id = 1)
public class TestClassFive {

  private final String message = "Some message";

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  public void repeatedParameterizedOne(boolean x) {
    assertTrue(x, message);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false, true})
  public void repeatedParameterizedTwo(boolean x) {
    assertTrue(x, message);
  }

  @RepeatedTest(2)
  public void repeatedOne() {
    assertTrue(false, message);
  }

  @RepeatedTest(1)
  public void repeatedTwo() {
    assertTrue(false, message);
  }

  @RepeatedTest(1)
  public void repeatedThree() {
    assertTrue(true, message);
  }
}
