package academy.kovalevskyi.testing.util;

import academy.kovalevskyi.testing.annotation.Container;
import academy.kovalevskyi.testing.annotation.CourseProvider;
import academy.kovalevskyi.testing.exception.ContainerNotFoundException;
import academy.kovalevskyi.testing.exception.NotAnnotatedContainerException;
import academy.kovalevskyi.testing.service.BaseComparator;
import academy.kovalevskyi.testing.service.Request;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;
import org.reflections.Reflections;

/**
 * Provides all available courses from package {@value COURSE_PACKAGE}.
 */
public class ContainerManager {

  private static final List<Class<?>> CONTAINERS;
  private static final String COURSE_PACKAGE = "academy.kovalevskyi";

  static {
    CONTAINERS = initialize();
  }

  /**
   * Provides all available containers.
   *
   * @return list of test classes
   * @throws ContainerNotFoundException if containers are absent
   */
  public static List<Class<?>> getContainers() {
    if (CONTAINERS.isEmpty()) {
      throw new ContainerNotFoundException("No available containers");
    }

    return CONTAINERS;
  }

  /**
   * Provides containers by request.
   *
   * @param request combined request of containers
   * @return list of test classes
   * @throws ContainerNotFoundException if containers are absent
   */
  public static List<Class<?>> getContainers(final Request request) {
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
   * Extracts an instance of {@link CourseProvider} implementation from class witch is annotated
   * with {@link Container} annotation.
   *
   * @param clazz test class
   * @return instance of {@link CourseProvider}
   * @throws ExceptionInInitializerError some edge situations
   */
  public static CourseProvider initProvider(final Class<?> clazz) {
    return initProvider(getAnnotation(clazz));
  }

  /**
   * Extracts an instance of {@link CourseProvider} implementation from class witch is annotated
   * with {@link Container} annotation.
   *
   * @param annotation {@link Container} instance
   * @return instance of {@link CourseProvider}
   * @throws ExceptionInInitializerError some edge situations
   */
  public static CourseProvider initProvider(final Container annotation) {
    try {
      return annotation.course().getConstructor().newInstance();
    } catch (NoSuchMethodException
        | InstantiationException
        | IllegalAccessException
        | InvocationTargetException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  /**
   * Extracts a {@link Container} instance from class witch is annotated with {@link Container}
   * annotation.
   *
   * @param clazz any class
   * @return {@link Container} instance
   * @throws NotAnnotatedContainerException if class is not annotated with {@link Container}
   */
  public static Container getAnnotation(final Class<?> clazz) {
    if (!clazz.isAnnotationPresent(Container.class)) {
      var message = String.format("%s is not annotated with @Container", clazz.getName());
      throw new NotAnnotatedContainerException(message);
    }

    return clazz.getAnnotation(Container.class);
  }

  private static List<Class<?>> initialize() {
    final var jcbPackage = "com.kovalevskyi.academy.codingbootcamp"; // TODO remove it later

    return new Reflections(COURSE_PACKAGE, jcbPackage)
        .getTypesAnnotatedWith(Container.class)
        .stream()
        .sorted(new BaseComparator())
        .collect(Collectors.toUnmodifiableList());
  }
}
