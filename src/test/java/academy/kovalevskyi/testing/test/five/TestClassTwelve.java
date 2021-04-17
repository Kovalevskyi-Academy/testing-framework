package academy.kovalevskyi.testing.test.five;

import academy.kovalevskyi.testing.annotation.Container;
import academy.kovalevskyi.testing.test.TestProvider;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

@Container(course = TestProvider.class, week = 4, day = 0, id = 5)
public class TestClassTwelve {

  private static int counter = 0;

  @Test
  public void noMethodOne() {
    throw new NoSuchMethodError("someMethod");
  }

  @RepeatedTest(2)
  public void noMethodTwo() {
    if (++counter == 2) {
      counter = 0;
      System.out.println("some text");
    }
    throw new NoSuchMethodError("someMethod");
  }

  @RepeatedTest(2)
  public void noMethodThree() {
    System.out.println("some text");
    throw new NoSuchMethodError("someMethod");
  }

  @RepeatedTest(2)
  public void noMethodFour() {
    throw new NoSuchMethodError("someMethod");
  }
}
