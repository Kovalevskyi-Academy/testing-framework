package academy.kovalevskyi.testing.annotation;

/**
 * Provides some basic information about a course.
 */
public interface ICourseProvider {

  /**
   * Course full name.
   *
   * @return name
   */
  String name();

  /**
   * Course unique key.
   *
   * @return uniqueId
   */
  String key();

  /**
   * Course unique id.
   *
   * @return id
   */
  int id();
}
