package academy.kovalevskyi.testing.test.five;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import academy.kovalevskyi.testing.annotation.Container;
import academy.kovalevskyi.testing.test.TestProvider;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

@Container(course = TestProvider.class, week = 4, day = 0, id = 0)
public class TestClassSeven {

  @Test
  public void withPrintsOk() {
    System.out.print("Some ok ");
    System.err.print("text");
    assertTrue(true);
  }

  @RepeatedTest(2)
  public void withRepeatedPrintsOk() {
    System.out.print("Some ok ");
    System.err.print("text");
    assertTrue(true);
  }

  @Test
  public void withPrintsBad() {
    System.out.print("Some bad ");
    System.err.print("text");
    assertTrue(false, "Some message");
  }

  @RepeatedTest(2)
  public void withRepeatedPrintsBad() {
    System.out.print("Some bad ");
    System.err.print("text");
    assertTrue(false, "Some message");
  }

  @Test
  public void withPrintsAborted() {
    System.out.print("Some aborted ");
    System.err.print("text");
    assumeTrue(false);
  }

  @RepeatedTest(2)
  public void withRepeatedPrintsAborted() {
    System.out.print("Some aborted ");
    System.err.print("text");
    assumeTrue(false);
  }

  @Disabled
  @Test
  public void withPrintsDisabled() {
    System.out.print("a");
    System.err.print("b");
    assertTrue(true);
  }
}
