package com.kovalevskyi.academy.testing.view;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TestsConsolePrinterTest {

  private TestsConsolePrinter testInstance;
  @BeforeEach
  void makeInstance() {
    testInstance = new TestsConsolePrinter();
  }

  @AfterEach
  void leanUp() {
    System.gc();
  }
  
  @Test
  void setSilentMode() {
    //TODO maybe remove this test?
  }

  @Test
  void testSuccessful() {
    // TODO mock for context.
    //  Catch print like in 'BarTest'.
  }

  @Test
  void testFailed() {
    //TODO
  }

  @Test
  void beforeAll() {
    //TODO
  }

  @Test
  void afterAll() {
    //TODO
  }

}