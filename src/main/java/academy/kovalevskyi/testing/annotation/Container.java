package academy.kovalevskyi.testing.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Serves for marking test containers. All test classes directly or indirectly need to inherit
 * AbstractTestExecutor. Class only annotation.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Container {

  /**
   * Class which provides some course information.
   *
   * @return some class
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
