package academy.kovalevskyi.testing.annotation;

/**
 * Provides some basic information about a course.
 */
public interface ICourseProvider {

  /**
   * Course name.
   *
   * @return name
   */
  String name();

  /**
   * Course id.
   *
   * @return id
   */
  int id();
}
