package com.kovalevskyi.academy.testing.foo;

import static com.google.common.truth.Truth.assertWithMessage;

import com.kovalevskyi.academy.testing.common.BasicStdTest;
import org.junit.jupiter.api.Test;

class BarTest extends BasicStdTest {

  @Test
  void somePrinter() {
    var expected = "test string from Bar.java\n";
    Bar.somePrinter();
    var actual = outputStreamCaptor.toString();
    assertWithMessage("Testing a 'testing-framework', a com.kovalevskyi.academy.testing.foo.Bar.java ")
        .that(actual)
        .isEqualTo(expected);
  }
}