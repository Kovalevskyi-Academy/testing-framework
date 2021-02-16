package academy.kovalevskyi.testing.service;

import academy.kovalevskyi.testing.model.AbstractContainer;
import academy.kovalevskyi.testing.util.ContainerManager;
import java.util.Comparator;

/**
 * Services to compare classes which is annotated with Container annotation by course/week/day/id
 */
public class BaseComparator implements Comparator<Class<? extends AbstractContainer>> {

  @Override
  public int compare(
      Class<? extends AbstractContainer> class1,
      Class<? extends AbstractContainer> class2) {
    final var container1 = ContainerManager.getAnnotation(class1);
    final var container2 = ContainerManager.getAnnotation(class2);
    final var course1 = ContainerManager.initProvider(container1);
    final var course2 = ContainerManager.initProvider(container2);

    if (!course1.key().equals(course2.key())) {
      return course1.key().compareTo(course2.key());
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
