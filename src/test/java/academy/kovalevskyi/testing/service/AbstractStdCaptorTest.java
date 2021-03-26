package academy.kovalevskyi.testing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AbstractStdCaptorTest {

  private static AbstractStdCaptor abstractStdCaptor;

  @BeforeAll
  public static void setUp() {
    abstractStdCaptor = new AbstractStdCaptor() {
    };
  }

  @Test
  public void testConfigOfSetUpMethods() {
    var beforeAll = false;
    var afterAll = false;
    for (var method : AbstractStdCaptor.class.getDeclaredMethods()) {
      if (method.isAnnotationPresent(BeforeAll.class)) {
        beforeAll = true;
        testPackageModifier(method);
      } else if (method.isAnnotationPresent(AfterAll.class)) {
        afterAll = true;
        testPackageModifier(method);
      }
    }

    assertTrue(beforeAll);
    assertTrue(afterAll);
  }

  @Test
  public void testConfigOfResetMethod() {
    var afterEach = false;
    for (var method : AbstractStdCaptor.class.getDeclaredMethods()) {
      if (method.isAnnotationPresent(AfterEach.class)) {
        afterEach = true;
        testPackageModifier(method);
      }
    }

    assertTrue(afterEach);
  }

  @Test
  public void testSystemOutputs() {
    var defaultOut = System.out;
    var defaultErr = System.err;

    AbstractStdCaptor.setUpCustomOutput();

    assertNotEquals(defaultOut, System.out);
    assertNotEquals(defaultErr, System.err);

    AbstractStdCaptor.setUpDefaultOutput();

    assertEquals(defaultOut, System.out);
    assertEquals(defaultErr, System.err);
  }

  @Test
  public void testSystemOutputsNotNull() {
    AbstractStdCaptor.setUpCustomOutput();
    assertNotNull(System.out);
    assertNotNull(System.err);
    AbstractStdCaptor.setUpDefaultOutput();
  }

  @Test
  public void testStreamIsClosed() {
    AbstractStdCaptor.setUpCustomOutput();
    var customOut = System.out;
    var customErr = System.err;
    abstractStdCaptor.resetBuffersData();
    AbstractStdCaptor.setUpDefaultOutput();

    customOut.write(10);
    customErr.write(10);

    assertTrue(abstractStdCaptor.getStdOutContent().isEmpty());
    assertTrue(abstractStdCaptor.getStdErrContent().isEmpty());
  }

  @Test
  public void testCaptorMainFunction() {
    AbstractStdCaptor.setUpCustomOutput();
    var expected = "some text";
    System.out.print(expected);
    System.err.print(expected);
    AbstractStdCaptor.setUpDefaultOutput();

    assertEquals(expected, abstractStdCaptor.getStdOutContent());
    assertEquals(expected, abstractStdCaptor.getStdErrContent());
  }

  @Test
  public void testCaptorHasDifferentBuffers() {
    AbstractStdCaptor.setUpCustomOutput();
    var stdOutText = "stdOut";
    var stdErrText = "stdErr";
    System.out.print(stdOutText);
    System.err.print(stdErrText);
    AbstractStdCaptor.setUpDefaultOutput();

    assertNotEquals(abstractStdCaptor.getStdOutContent(), abstractStdCaptor.getStdErrContent());
  }

  @Test
  public void testCaptorResetBuffersAfterEachTest() {
    AbstractStdCaptor.setUpCustomOutput();
    System.out.print("test");
    System.err.print("test");
    abstractStdCaptor.resetBuffersData();
    AbstractStdCaptor.setUpDefaultOutput();

    assertTrue(abstractStdCaptor.getStdOutContent().isEmpty());
    assertTrue(abstractStdCaptor.getStdErrContent().isEmpty());
  }

  private void testPackageModifier(Method method) {
    assertFalse(Modifier.isPrivate((method.getModifiers())));
    assertFalse(Modifier.isPublic((method.getModifiers())));
    assertFalse(Modifier.isProtected((method.getModifiers())));
  }
}
