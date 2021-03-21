package academy.kovalevskyi.testing.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import academy.kovalevskyi.testing.annotation.Container;
import academy.kovalevskyi.testing.exception.ContainerNotFoundException;
import academy.kovalevskyi.testing.exception.NotAnnotatedContainerException;
import academy.kovalevskyi.testing.service.ContainerRequest;
import academy.kovalevskyi.testing.test.TestProvider;
import academy.kovalevskyi.testing.test.one.TestClassOne;
import academy.kovalevskyi.testing.test.one.TestClassTwo;
import academy.kovalevskyi.testing.test.three.TestClassFour;
import academy.kovalevskyi.testing.test.two.TestClassThree;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ContainerManagerTest {

  @Test
  public void testGetAnnotation() {
    var expected = TestClassOne.class.getAnnotation(Container.class);
    assertEquals(expected, ContainerManager.getAnnotation(TestClassOne.class));
  }

  @Test
  public void testGetAnnotationIllegalArgument() {
    try {
      ContainerManager.getAnnotation(ContainerManagerTest.class);
      fail();
    } catch (NotAnnotatedContainerException ignored) {
    }
  }

  @Test
  public void testInitProviderWithClassParameter() {
    var expected = new TestProvider();
    var actual = ContainerManager.initProvider(TestClassOne.class);
    assertEquals(expected.key(), actual.key());
    assertEquals(expected.name(), actual.name());
  }

  @Test
  public void testInitProviderWithAnnotationParameter() {
    var expected = new TestProvider();
    var actual = ContainerManager.initProvider(TestClassOne.class.getAnnotation(Container.class));
    assertEquals(expected.key(), actual.key());
    assertEquals(expected.name(), actual.name());
  }

  @Test
  public void testInitProviderWithIllegalClassParameter() {
    try {
      ContainerManager.initProvider(ContainerManagerTest.class);
      fail();
    } catch (NotAnnotatedContainerException ignored) {
    }
  }

  @Test
  public void testGetAllContainersInRightOrder() {
    var expected = getAllContainersInRightOrder();
    var actual = ContainerManager.getContainers();
    assertArrayEquals(expected.toArray(), actual.toArray());
  }

  @Test
  public void testGetAllContainersInRightOrderWithPackageParam() {
    var expected = getAllContainersInRightOrder();
    var actual = ContainerManager.getContainers("academy.kovalevskyi.testing.test");
    assertArrayEquals(expected.toArray(), actual.toArray());
  }

  @Test
  public void testGetSpecifyContainer() {
    var request = ContainerRequest.builder().course(TestProvider.KEY).week(2).day(2).build();
    var list = ContainerManager.getContainers(request);
    assertEquals(1, list.size());
    assertEquals(TestClassFour.class, list.get(0));
  }

  @Test
  public void testGetAllContainersInRightOrderWithRequest() {
    var request = ContainerRequest.builder().course(TestProvider.KEY).build();
    var expected = getAllContainersInRightOrder();
    var actual = ContainerManager.getContainers(request);
    assertArrayEquals(expected.toArray(), actual.toArray());
  }

  @Test
  public void testGetAllContainersInRightOrderWithRequestAndPackageParam() {
    var request = ContainerRequest.builder().course(TestProvider.KEY).build();
    var expected = getAllContainersInRightOrder();
    var actual = ContainerManager.getContainers(request, "academy.kovalevskyi.testing.test");
    assertArrayEquals(expected.toArray(), actual.toArray());
  }

  @Test
  public void testNoContainers() {
    var request = ContainerRequest.builder().course("SOME_KEY").build();
    try {
      ContainerManager.getContainers(request);
      fail();
    } catch (ContainerNotFoundException ignored) {
    }
  }

  @Test
  public void testReturnListIsUnmodifiable() {
    try {
      ContainerManager.getContainers().remove(0);
      fail();
    } catch (UnsupportedOperationException ignored) {
    }
  }

  @Test
  public void testReturnListIsUnmodifiableWithRequest() {
    var request = ContainerRequest.builder().course(TestProvider.KEY).build();
    try {
      ContainerManager.getContainers(request).remove(0);
      fail();
    } catch (UnsupportedOperationException ignored) {
    }
  }

  private List<Class<?>> getAllContainersInRightOrder() {
    var original = new ArrayList<Class<?>>();
    original.add(TestClassOne.class);
    original.add(TestClassTwo.class);
    original.add(TestClassThree.class);
    original.add(TestClassFour.class);
    return original;
  }
}