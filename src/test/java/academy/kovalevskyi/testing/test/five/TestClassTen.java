package academy.kovalevskyi.testing.test.five;

import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.kovalevskyi.testing.annotation.Container;
import academy.kovalevskyi.testing.test.TestProvider;
import org.junit.jupiter.api.Test;

@Container(course = TestProvider.class, week = 4, day = 0, id = 3)
public class TestClassTen {

  @Test
  public void withNoMessage() throws Exception {
    throw new Exception();
  }

  @Test
  public void withMessage() throws Exception {
    throw new Exception("Some message");
  }

  @Test
  public void successful() {
    assertTrue(true);
  }
}
