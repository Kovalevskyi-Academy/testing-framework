package academy.kovalevskyi.testing.annotation;

import academy.kovalevskyi.testing.service.ContainerHandler;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Serves for marking test classes. All test classes should be annotated with this annotation to
 * work with Testing Framework.
 */
@ExtendWith(ContainerHandler.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Container {

  /**
   * Class which provides some important information of course.
   *
   * @return any class which implements {@link ICourseProvider}
   */
  Class<? extends ICourseProvider> course();

  /**
   * Provides week number of container.
   *
   * @return week number
   */
  int week();

  /**
   * Provides day number of container.
   *
   * @return day number
   */
  int day();

  /**
   * Provides container id.
   *
   * @return container number
   */
  int id();
}
