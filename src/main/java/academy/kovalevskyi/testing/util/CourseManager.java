package academy.kovalevskyi.testing.util;

import academy.kovalevskyi.testing.AbstractTestExecutor;
import academy.kovalevskyi.testing.annotation.Container;
import academy.kovalevskyi.testing.annotation.ICourseProvider;
import academy.kovalevskyi.testing.exception.ContainerNotFoundException;
import academy.kovalevskyi.testing.exception.NotAnnotatedContainerException;
import academy.kovalevskyi.testing.service.IRequest;
import academy.kovalevskyi.testing.service.ContainerComparator;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;
import org.reflections.Reflections;

/**
 * Provides all available courses from packages {@value COURSE_PACKAGE}.
 */
public class CourseManager {

  private static final List<Class<? extends AbstractTestExecutor>> CONTAINERS;
  private static final String COURSE_PACKAGE = "academy.kovalevskyi.course";

  static {
    CONTAINERS = initialize();
  }

  /**
   * Provides all available containers.
   *
   * @return list of classes of containers
   * @throws ContainerNotFoundException if containers are absent
   */
  public static List<Class<? extends AbstractTestExecutor>> getContainers() {
    if (CONTAINERS.isEmpty()) {
      throw new ContainerNotFoundException("List of containers is empty");
    }

    return CONTAINERS;
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

  private static List<Class<? extends AbstractTestExecutor>> initialize() {
    final var jcbPackage = "com.kovalevskyi.academy.codingbootcamp"; // TODO remove it later
    final var jddPackage = "academy.kovalevskyi.javadeepdive"; // TODO remove it later

    return new Reflections(jcbPackage, jddPackage) // TODO change prefix to COURSE_PACKAGE variable
        .getSubTypesOf(AbstractTestExecutor.class)
        .stream()
        .filter(clazz -> clazz.isAnnotationPresent(Container.class))
        .sorted(new ContainerComparator())
        .collect(Collectors.toUnmodifiableList());
  }
}
