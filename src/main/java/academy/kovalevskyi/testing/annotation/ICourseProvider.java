package academy.kovalevskyi.testing.annotation;

/**
 * Provides some basic information about a course.
 */
public interface ICourseProvider {

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
