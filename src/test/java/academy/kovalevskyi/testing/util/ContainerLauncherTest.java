package academy.kovalevskyi.testing.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import academy.kovalevskyi.testing.exception.ContainerNotFoundException;
import academy.kovalevskyi.testing.exception.NotAnnotatedContainerException;
import academy.kovalevskyi.testing.service.FrameworkProperty;
import academy.kovalevskyi.testing.test.one.TestClassOne;
import academy.kovalevskyi.testing.test.three.TestClassFourDuplicate;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class ContainerLauncherTest {

  @Test
  public void testExecuteContainerSystemProperties() {
    disableAllProperties();
    ContainerLauncher.execute(TestClassOne.class, true, true, true);
    checkAllPropertiesIsTrue();
  }

  @Test
  public void testExecuteContainersSystemProperties() {
    disableAllProperties();
    var list = new ArrayList<Class<?>>();
    list.add(TestClassOne.class);
    ContainerLauncher.execute(list, true, true, true);
    checkAllPropertiesIsTrue();
  }

  @Test
  public void testExecuteContainerWithUnsupportedClass() {
    try {
      ContainerLauncher.execute(ContainerLauncherTest.class, true, true, true);
      fail();
    } catch (NotAnnotatedContainerException ignored) {
    }
  }

  @Test
  public void testExecuteContainersWithUnsupportedClass() {
    try {
      var list = new ArrayList<Class<?>>();
      list.add(ContainerLauncherTest.class);
      ContainerLauncher.execute(list, true, true, true);
      fail();
    } catch (NotAnnotatedContainerException ignored) {
    }
  }

  @Test
  public void testExecuteContainersWithEmptyList() {
    try {
      ContainerLauncher.execute(Collections.emptyList(), true, true, true);
      fail();
    } catch (ContainerNotFoundException ignored) {
    }
  }

  @Test
  public void testExecuteContainerLaunchedTest() {
    ContainerLauncher.execute(TestClassFourDuplicate.class, true, true, true);
    assertTrue(TestClassFourDuplicate.TEST_FILE.exists());
    TestClassFourDuplicate.TEST_FILE.deleteOnExit();
  }

  @Test
  public void testExecuteContainersLaunchedTest() {
    var list = new ArrayList<Class<?>>();
    list.add(TestClassFourDuplicate.class);
    ContainerLauncher.execute(list, true, true, true);
    assertTrue(TestClassFourDuplicate.TEST_FILE.exists());
    TestClassFourDuplicate.TEST_FILE.deleteOnExit();
  }

  private void disableAllProperties() {
    System.setProperty(FrameworkProperty.VERBOSE_MODE, Boolean.toString(false));
    System.setProperty(FrameworkProperty.DEBUG_MODE, Boolean.toString(false));
    System.setProperty(FrameworkProperty.ERROR_MODE, Boolean.toString(false));
  }

  private void checkAllPropertiesIsTrue() {
    assertEquals(System.getProperty(FrameworkProperty.VERBOSE_MODE), Boolean.toString(true));
    assertEquals(System.getProperty(FrameworkProperty.DEBUG_MODE), Boolean.toString(true));
    assertEquals(System.getProperty(FrameworkProperty.ERROR_MODE), Boolean.toString(true));
  }
}