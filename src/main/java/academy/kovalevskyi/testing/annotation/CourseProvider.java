package academy.kovalevskyi.testing.annotation;

/**
 * Provides important information about some course.
 */
public interface CourseProvider {

  /**
   * Course full name.
   *
   * @return name of course
   */
  String name();

  /**
   * Course unique key.
   *
   * @return key of course
   */
  String key();
}
