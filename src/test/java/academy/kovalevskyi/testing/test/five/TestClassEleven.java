package academy.kovalevskyi.testing.test.five;

import academy.kovalevskyi.testing.annotation.Container;
import academy.kovalevskyi.testing.test.TestProvider;
import org.junit.jupiter.api.Test;

@Container(course = TestProvider.class, week = 4, day = 0, id = 4)
public class TestClassEleven {

  @Test
  public void noClass() {
    throw new NoClassDefFoundError("SomeClass");
  }
}
