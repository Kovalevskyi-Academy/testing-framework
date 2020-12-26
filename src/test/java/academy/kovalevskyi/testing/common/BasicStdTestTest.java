package academy.kovalevskyi.testing.common;

import static com.google.common.truth.Truth.assertWithMessage;

import org.junit.jupiter.api.Test;

public class BasicStdTestTest extends BasicStdTest {

  @Test
  public void somePrinter() {
    var expected = "test string from Bar.java\n";
    System.out.print(expected);
    var actual = outputStreamCaptor.toString();
    assertWithMessage("Testing a 'testing-framework', a com.kovalevskyi.academy.testing.foo.Bar.java ")
        .that(actual)
        .isEqualTo(expected);
  }
}