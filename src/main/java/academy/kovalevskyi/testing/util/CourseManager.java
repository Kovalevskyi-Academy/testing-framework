package academy.kovalevskyi.testing.util;

import academy.kovalevskyi.testing.AbstractTestExecutor;
import academy.kovalevskyi.testing.annotation.Container;
import academy.kovalevskyi.testing.annotation.ICourseProvider;
import academy.kovalevskyi.testing.common.BasicStdTest;
import academy.kovalevskyi.testing.exception.ContainerNotFoundException;
import academy.kovalevskyi.testing.exception.NotAnnotatedContainerException;
import academy.kovalevskyi.testing.service.IRequest;
import academy.kovalevskyi.testing.service.ContainerComparator;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.reflections.Reflections;

/**
 * Provides all available courses from packages {@value FIRST_PACKAGE,SECOND_PACKAGE}.
 */
public class CourseManager {

  private static final Set<Class<? extends AbstractTestExecutor>> CONTAINERS;

  // TODO change base package for all courses to 'academy.kovalevskyi.course'
  private static final String FIRST_PACKAGE = "academy.kovalevskyi.javadeepdive";
  private static final String SECOND_PACKAGE = "com.kovalevskyi.academy.codingbootcamp";

  static {
    var reflections = new Reflections(FIRST_PACKAGE, SECOND_PACKAGE);
    CONTAINERS = new TreeSet<>(new ContainerComparator());
    // TODO after changing base package for courses code below should be deleted
    // to remove BasicStdTest class from list to fix an error (temporary solution)
    var tmp = reflections
        .getSubTypesOf(AbstractTestExecutor.class)
        .stream()
        .filter(clazz -> !clazz.equals(BasicStdTest.class))
        .collect(Collectors.toSet());
    CONTAINERS.addAll(tmp);
  }

  /**
   * Provides all available containers.
   *
   * @return list of classes of containers
   * @throws ContainerNotFoundException if containers are absent
   */
  public static List<Class<? extends AbstractTestExecutor>> getContainers() {
    final var result = CONTAINERS.stream().collect(Collectors.toUnmodifiableList());

    if (result.isEmpty()) {
      throw new ContainerNotFoundException("Container's list is empty");
    }

    return result;
  }

  /**
   * Provides containers by request.
   *
   * @param request combined request
   * @return list of classes of containers
   * @throws ContainerNotFoundException if containers are absent
   */
  public static List<Class<? extends AbstractTestExecutor>> getContainers(final IRequest request) {
    final var result = CONTAINERS
        .stream()
        .filter(request.getPredicate())
        .collect(Collectors.toUnmodifiableList());

    if (result.isEmpty()) {
      throw new ContainerNotFoundException("Containers are not found by your request");
    }

    return result;
  }

  /**
   * Extracts an instance of ICourseProvider implementation from class witch is annotated with
   * Container annotation.
   *
   * @param clazz AbstractTestExecutor heir class
   * @return instance of ICourseProvider
   * @throws ExceptionInInitializerError some edge situations
   */
  public static ICourseProvider initProvider(final Class<? extends AbstractTestExecutor> clazz) {
    return initProvider(getAnnotation(clazz));
  }

  /**
   * Extracts an instance of ICourseProvider implementation from class witch is annotated with
   * Container annotation.
   *
   * @param annotation Container instance
   * @return instance of ICourseProvider
   * @throws ExceptionInInitializerError some edge situations
   */
  public static ICourseProvider initProvider(final Container annotation) {
    try {
      return (ICourseProvider) annotation.course().getConstructors()[0].newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new ExceptionInInitializerError(e.getMessage());
    }
  }

  /**
   * Extracts a Container instance from class witch is annotated with Container annotation.
   *
   * @param clazz any heir of AbstractTestExecutor class
   * @return Container instance
   * @throws NotAnnotatedContainerException if class is not annotated with @Container
   */
  public static Container getAnnotation(final Class<? extends AbstractTestExecutor> clazz) {
    if (!clazz.isAnnotationPresent(Container.class)) {
      var message = String.format("%s is not annotated with @Container", clazz.getName());
      throw new NotAnnotatedContainerException(message);
    }
    return clazz.getAnnotation(Container.class);
  }
}
