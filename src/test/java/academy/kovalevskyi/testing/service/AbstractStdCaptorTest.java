package academy.kovalevskyi.testing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AbstractStdCaptorTest {

  private static AbstractStdCaptor abstractStdCaptor;

  @BeforeAll
  public static void setUp() {
    abstractStdCaptor = new AbstractStdCaptor() {
    };
  }

  @Test
  public void testSetUpMethodsPresent() {
    var beforeEach = (Method) null;
    var afterEach = (Method) null;
    for (var method : AbstractStdCaptor.class.getDeclaredMethods()) {
      if (method.isAnnotationPresent(BeforeEach.class)) {
        beforeEach = method;
      } else if (method.isAnnotationPresent(AfterEach.class)) {
        afterEach = method;
      }
    }

    assertNotNull(beforeEach);
    assertNotNull(afterEach);
  }

  @Test
  public void testVisibilityOfSetUpMethods() {
    for (var method : AbstractStdCaptor.class.getDeclaredMethods()) {
      if (method.isAnnotationPresent(BeforeEach.class)
          || method.isAnnotationPresent(AfterEach.class)) {
        assertFalse(Modifier.isPrivate((method.getModifiers())));
        assertFalse(Modifier.isPublic((method.getModifiers())));
        assertFalse(Modifier.isProtected((method.getModifiers())));
      }
    }
  }

  @Test
  public void testSystemOutputs() {
    var defaultOut = System.out;
    var defaultErr = System.err;

    abstractStdCaptor.setUpCustomOutput();
    assertNotEquals(defaultOut, System.out);
    assertNotEquals(defaultErr, System.err);

    abstractStdCaptor.setUpDefaultOutput();
    assertEquals(defaultOut, System.out);
    assertEquals(defaultErr, System.err);
  }

  @Test
  public void testSystemOutputsNotNull() {
    abstractStdCaptor.setUpCustomOutput();
    assertNotNull(System.out);
    assertNotNull(System.err);
    abstractStdCaptor.setUpDefaultOutput();
  }

  @Test
  public void testStreamIsClosed() {
    abstractStdCaptor.setUpCustomOutput();
    var customOut = System.out;
    var customErr = System.err;
    abstractStdCaptor.setUpDefaultOutput();

    customOut.write(7);
    customErr.write(7);

    assertTrue(abstractStdCaptor.getStdOutText().isEmpty());
    assertTrue(abstractStdCaptor.getStdErrText().isEmpty());
  }

  @Test
  public void testCaptorMainFunction() {
    abstractStdCaptor.setUpCustomOutput();
    var expected = "some text";
    System.out.print(expected);
    System.err.print(expected);
    abstractStdCaptor.setUpDefaultOutput();

    assertEquals(expected, abstractStdCaptor.getStdOutText());
    assertEquals(expected, abstractStdCaptor.getStdErrText());
  }

  @Test
  public void testCaptorHasDifferentBuffers() {
    abstractStdCaptor.setUpCustomOutput();
    var stdOutText = "stdOut";
    var stdErrText = "stdErr";
    System.out.print(stdOutText);
    System.err.print(stdErrText);
    abstractStdCaptor.setUpDefaultOutput();

    assertNotEquals(abstractStdCaptor.getStdOutText(), abstractStdCaptor.getStdErrText());
  }

  @Test
  public void testCaptorResetBuffersAfterEachTest() {
    abstractStdCaptor.setUpCustomOutput();
    System.out.print("test");
    System.err.print("test");
    abstractStdCaptor.setUpDefaultOutput();

    abstractStdCaptor.setUpCustomOutput();
    abstractStdCaptor.setUpDefaultOutput();

    assertTrue(abstractStdCaptor.getStdOutText().isEmpty());
    assertTrue(abstractStdCaptor.getStdErrText().isEmpty());
  }

}
