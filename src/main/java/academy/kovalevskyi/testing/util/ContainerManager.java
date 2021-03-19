package academy.kovalevskyi.testing.util;

import academy.kovalevskyi.testing.annotation.Container;
import academy.kovalevskyi.testing.annotation.CourseProvider;
import academy.kovalevskyi.testing.exception.ContainerNotFoundException;
import academy.kovalevskyi.testing.exception.NotAnnotatedContainerException;
import academy.kovalevskyi.testing.service.BaseComparator;
import academy.kovalevskyi.testing.service.Request;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.reflections.Reflections;

/**
 * Provides all available test containers.
 */
public final class ContainerManager {

  /**
   * Provides all available containers from project or some packages.
   *
   * @param packages package prefixes
   * @return list of test classes
   * @throws ContainerNotFoundException if containers are absent
   */
  public static List<Class<?>> getContainers(final String... packages) {
    return findContainers(packages).collect(Collectors.toUnmodifiableList());
  }

  /**
   * Provides containers by request from project or some packages.
   *
   * @param request  combined request of containers
   * @param packages package prefixes
   * @return list of test classes
   * @throws ContainerNotFoundException if containers are absent
   */
  public static List<Class<?>> getContainers(final Request request, final String... packages) {
    final var result = findContainers(packages)
        .filter(request.getPredicate())
        .collect(Collectors.toUnmodifiableList());

    if (result.isEmpty()) {
      final var textRepresentation = request.toString();
      final var memoryLinkOfObject = Integer.toHexString(System.identityHashCode(request));
      throw new ContainerNotFoundException(
          String.format(
              "Containers are not found by your request %s",
              textRepresentation.endsWith(memoryLinkOfObject) ? "\b" : textRepresentation));
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
   * Extracts an instance of {@link CourseProvider} implementation from {@link Container}
   * annotation.
   *
   * @param annotation {@link Container} instance
   * @return instance of {@link CourseProvider}
   * @throws ExceptionInInitializerError some edge situations
   */
  public static CourseProvider initProvider(final Container annotation) {
    try {
      return annotation.course().getConstructor().newInstance();
    } catch (Exception e) {
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

  private static Stream<Class<?>> findContainers(final String... packages) {
    final var reflections = new Reflections(packages.length == 0 ? "" : packages);
    final var containers = reflections.getTypesAnnotatedWith(Container.class);

    if (containers.isEmpty()) {
      throw new ContainerNotFoundException("No available containers");
    }

    return containers.stream().sorted(new BaseComparator());
  }
}
