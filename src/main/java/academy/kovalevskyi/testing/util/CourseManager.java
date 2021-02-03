package academy.kovalevskyi.testing.util;

import academy.kovalevskyi.testing.AbstractTestExecutor;
import academy.kovalevskyi.testing.annotation.Container;
import academy.kovalevskyi.testing.annotation.ICourseProvider;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.reflections.Reflections;

/**
 * Provides all available courses from package {@value TEST_CLASSES_PACKAGE}.
 */
public class CourseManager {

  private static final Set<Class<? extends AbstractTestExecutor>> TEST_CLASSES;
  private static final String TEST_CLASSES_PACKAGE = "academy.kovalevskyi.course"; // TODO change base package for all courses to 'academy.kovalevskyi.course'

  static {
    TEST_CLASSES = new Reflections(TEST_CLASSES_PACKAGE).getSubTypesOf(AbstractTestExecutor.class);
  }

  /**
   * For service goals only. Prints classes which are not annotated with @CourseManager to find and
   * fix bugs before packaging a project. Best practice it is using it in main() method ;)
   */
  public static void printNotAnnotatedClasses() {
    TEST_CLASSES
        .stream()
        .filter(entry -> !entry.isAnnotationPresent(Container.class))
        .map(Class::getName)
        .sorted()
        .forEach(System.out::println);
  }

  /**
   * Provides all containers by course id.
   *
   * @param course course id
   * @return list of classes of containers
   */
  public static List<Class<? extends AbstractTestExecutor>> getContainersBy(final int course) {
    return getCourse(course).collect(Collectors.toUnmodifiableList());
  }

  /**
   * Provides all containers by course id/week/day.
   *
   * @param course course id
   * @param week   week number
   * @param day    day number
   * @return list of classes of containers
   */
  public static List<Class<? extends AbstractTestExecutor>> getContainersBy(
      final int course,
      final int week,
      final int day) {
    return getCourse(course)
        .filter(clazz -> {
          final var annotation = clazz.getAnnotation(Container.class);
          return annotation.week() == week && annotation.day() == day;
        })
        .collect(Collectors.toUnmodifiableList());
  }

  /**
   * Provides all containers by course id/week/day/container.
   *
   * @param course    course id
   * @param week      week number
   * @param day       day number
   * @param container container id
   * @return list of classes of containers
   * @throws java.util.NoSuchElementException if container is absent
   */
  public static Class<? extends AbstractTestExecutor> getContainerBy(
      final int course,
      final int week,
      final int day,
      final int container) {
    return getCourse(course)
        .filter(clazz -> {
          final var annotation = clazz.getAnnotation(Container.class);
          return annotation.week() == week
              && annotation.day() == day
              && annotation.id() == container;
        })
        .findFirst().orElseThrow();
  }

  private static Stream<Class<? extends AbstractTestExecutor>> getCourse(final int id) {
    return TEST_CLASSES
        .stream()
        .filter(clazz -> clazz.isAnnotationPresent(Container.class))
        .filter(clazz -> initProvider(clazz.getAnnotation(Container.class)).id() == id)
        .sorted(Comparator
            .comparingInt((Class<?> clazz) -> clazz.getAnnotation(Container.class).week())
            .thenComparingInt(clazz -> clazz.getAnnotation(Container.class).day())
            .thenComparingInt(clazz -> clazz.getAnnotation(Container.class).id()));
  }

  private static ICourseProvider initProvider(Container manager) {
    try {
      return (ICourseProvider) manager.course().getConstructors()[0].newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new ExceptionInInitializerError(e.getMessage());
    }
  }
}