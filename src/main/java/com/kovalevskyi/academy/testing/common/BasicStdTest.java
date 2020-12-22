package com.kovalevskyi.academy.testing.common;

import com.kovalevskyi.academy.testing.AbstractTestExecutor;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.BeforeEach;


public abstract class BasicStdTest extends AbstractTestExecutor {

  protected final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

  @BeforeEach
  public void setUp() {
    System.setOut(new PrintStream(outputStreamCaptor));
  }
}