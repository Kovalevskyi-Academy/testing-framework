package academy.kovalevskyi.testing.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import academy.kovalevskyi.testing.annotation.Container;
import academy.kovalevskyi.testing.exception.ContainerNotFoundException;
import academy.kovalevskyi.testing.exception.NotAnnotatedContainerException;
import academy.kovalevskyi.testing.service.ContainerRequest;
import academy.kovalevskyi.testing.test.TestProvider;
import academy.kovalevskyi.testing.test.five.TestClassEight;
import academy.kovalevskyi.testing.test.five.TestClassEleven;
import academy.kovalevskyi.testing.test.five.TestClassNine;
import academy.kovalevskyi.testing.test.five.TestClassSeven;
import academy.kovalevskyi.testing.test.five.TestClassTen;
import academy.kovalevskyi.testing.test.five.TestClassThirteen;
import academy.kovalevskyi.testing.test.five.TestClassTwelve;
import academy.kovalevskyi.testing.test.four.TestClassSix;
import academy.kovalevskyi.testing.test.one.TestClassOne;
import academy.kovalevskyi.testing.test.one.TestClassTwo;
import academy.kovalevskyi.testing.test.three.TestClassFive;
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
    assertThrows(NotAnnotatedContainerException.class,
        () -> ContainerManager.getAnnotation(ContainerManagerTest.class));
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
    assertThrows(NotAnnotatedContainerException.class,
        () -> ContainerManager.initProvider(ContainerManagerTest.class));
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
    assertEquals(2, list.size());
    assertEquals(TestClassFour.class, list.get(0));
    assertEquals(TestClassFive.class, list.get(1));
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
  public void testNoContainersWithRequest() {
    var request = ContainerRequest.builder().course("SOME_KEY").build();
    assertThrows(ContainerNotFoundException.class,
        () -> ContainerManager.getContainers(request));
  }

  @Test
  public void testReturnListIsUnmodifiable() {
    assertThrows(UnsupportedOperationException.class,
        () -> ContainerManager.getContainers().remove(0));
  }

  @Test
  public void testReturnListIsUnmodifiableWithRequest() {
    var request = ContainerRequest.builder().course(TestProvider.KEY).build();
    assertThrows(UnsupportedOperationException.class,
        () -> ContainerManager.getContainers(request).remove(0));
  }

  private List<Class<?>> getAllContainersInRightOrder() {
    var original = new ArrayList<Class<?>>();
    original.add(TestClassOne.class);
    original.add(TestClassTwo.class);
    original.add(TestClassThree.class);
    original.add(TestClassFour.class);
    original.add(TestClassFive.class);
    original.add(TestClassSix.class);
    original.add(TestClassSeven.class);
    original.add(TestClassEight.class);
    original.add(TestClassNine.class);
    original.add(TestClassTen.class);
    original.add(TestClassEleven.class);
    original.add(TestClassTwelve.class);
    original.add(TestClassThirteen.class);
    return original;
  }
}