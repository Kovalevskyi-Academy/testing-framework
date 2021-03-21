package academy.kovalevskyi.testing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import academy.kovalevskyi.testing.annotation.Container;
import academy.kovalevskyi.testing.exception.RequestException;
import academy.kovalevskyi.testing.test.TestProvider;
import academy.kovalevskyi.testing.test.one.TestClassOne;
import org.junit.jupiter.api.Test;

public class ContainerRequestTest {

  @Test
  public void testTextRepresentation() {
    var request = ContainerRequest.builder().course("KEY").week(0).day(1).container(2).build();
    assertEquals("[key - KEY, week - 0, day - 1, id - 2]", request.toString());
  }

  @Test
  public void testPredicate() {
    var container = TestClassOne.class.getAnnotation(Container.class);
    var request = ContainerRequest.builder()
        .course(TestProvider.KEY.toLowerCase())
        .week(container.week())
        .day(container.day())
        .container(container.id())
        .build();
    assertTrue(request.getPredicate().test(TestClassOne.class));
  }

  @Test
  public void testBuilderCourseMissing() {
    try {
      ContainerRequest.builder().build();
      fail();
    } catch (RequestException ignored) {
    }
  }

  @Test
  public void testBuilderCourseEmpty() {
    try {
      ContainerRequest.builder().course("").build();
      fail();
    } catch (RequestException ignored) {
    }
  }

  @Test
  public void testBuilderCourseBlank() {
    try {
      ContainerRequest.builder().course("  ").build();
      fail();
    } catch (RequestException ignored) {
    }
  }

  @Test
  public void testBuilderCourseWithWhitespaces() {
    try {
      ContainerRequest.builder().course("key 1").build();
      fail();
    } catch (RequestException ignored) {
    }
  }

  @Test
  public void testBuilderWeekMissing() {
    try {
      ContainerRequest.builder().course("key").day(0).build();
      fail();
    } catch (RequestException ignored) {
    }
  }

  @Test
  public void testBuilderWithIdWeekMissing() {
    try {
      ContainerRequest.builder().course("key").day(0).container(0).build();
      fail();
    } catch (RequestException ignored) {
    }
  }

  @Test
  public void testBuilderWithIdDayMissing() {
    try {
      ContainerRequest.builder().course("key").week(0).container(0).build();
      fail();
    } catch (RequestException ignored) {
    }
  }
}