package academy.kovalevskyi.testing.service;

import academy.kovalevskyi.testing.AbstractTestExecutor;
import academy.kovalevskyi.testing.util.CourseManager;
import java.util.Comparator;

/**
 * Services to compare classes which is annotated with Container annotation by course/week/day/id
 */
public class ContainerComparator implements Comparator<Class<? extends AbstractTestExecutor>> {

  @Override
  public int compare(
      Class<? extends AbstractTestExecutor> class1,
      Class<? extends AbstractTestExecutor> class2) {
    final var container1 = CourseManager.getAnnotation(class1);
    final var container2 = CourseManager.getAnnotation(class2);
    final var course1 = CourseManager.initProvider(container1);
    final var course2 = CourseManager.initProvider(container2);

    if (course1.id() != course2.id()) {
      return course1.id() - course2.id();
    }
    if (container1.week() != container2.week()) {
      return container1.week() - container2.week();
    }
    if (container1.day() != container2.day()) {
      return container1.day() - container2.day();
    }
    return container1.id() - container2.id();
  }

}
