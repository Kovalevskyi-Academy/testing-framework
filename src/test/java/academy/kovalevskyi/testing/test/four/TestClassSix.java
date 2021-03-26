package academy.kovalevskyi.testing.test.four;

import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.kovalevskyi.testing.annotation.Container;
import academy.kovalevskyi.testing.test.TestProvider;
import org.junit.jupiter.api.Test;

@Container(course = TestProvider.class, week = 3, day = 2, id = 0)
public class TestClassSix {

  @Test
  public void simpleOne() {
    assertTrue(false, "!".repeat(601));
  }

  @Test
  public void simpleTwo() {
    assertTrue(false, "     \n\n\n Some message");
  }
}
