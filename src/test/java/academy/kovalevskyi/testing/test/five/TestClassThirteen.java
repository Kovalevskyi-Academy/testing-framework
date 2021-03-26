package academy.kovalevskyi.testing.test.five;

import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.kovalevskyi.testing.annotation.Container;
import academy.kovalevskyi.testing.test.TestProvider;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@Container(course = TestProvider.class, week = 4, day = 0, id = 6)
public class TestClassThirteen {

  @Test
  public void simple() {
    System.out.print("Some ok ");
    System.err.print("text");
    assertTrue(true);
  }

  @RepeatedTest(2)
  public void repeated() {
    assertTrue(true);
  }

  @ParameterizedTest
  @NullSource
  public void parameterized(Object object) {
    assertTrue(true);
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2})
  public void repeatedParameterized(int x) {
    assertTrue(true);
  }
}
