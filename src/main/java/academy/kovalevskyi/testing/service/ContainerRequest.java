package academy.kovalevskyi.testing.service;

import academy.kovalevskyi.testing.AbstractTestExecutor;
import academy.kovalevskyi.testing.exception.RequestException;
import academy.kovalevskyi.testing.util.CourseManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Custom request which can filter container classes by course/week/day/id.
 */
public class ContainerRequest implements IRequest {

  private final Predicate<Class<? extends AbstractTestExecutor>> predicate;

  private ContainerRequest(Predicate<Class<? extends AbstractTestExecutor>> predicate) {
    this.predicate = predicate;
  }

  @Override
  public Predicate<Class<? extends AbstractTestExecutor>> getPredicate() {
    return predicate;
  }

  /**
   * ContainerRequest builder.
   *
   * @return builder
   */
  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private boolean courseInitialized;
    private final List<Predicate<Class<? extends AbstractTestExecutor>>> predicates;

    private Builder() {
      this.predicates = new ArrayList<>();
    }

    public Builder course(int id) {
      checkCourseInitialization();
      this.predicates.add(clazz -> CourseManager.initProvider(clazz).id() == id);
      return this;
    }

    public Builder course(String key) {
      checkCourseInitialization();
      this.predicates.add(clazz -> CourseManager.initProvider(clazz).key().equalsIgnoreCase(key));
      return this;
    }

    public Builder week(int week) {
      this.predicates.add(clazz -> CourseManager.getAnnotation(clazz).week() == week);
      return this;
    }

    public Builder day(int day) {
      this.predicates.add(clazz -> CourseManager.getAnnotation(clazz).day() == day);
      return this;
    }

    public Builder id(int id) {
      this.predicates.add(clazz -> CourseManager.getAnnotation(clazz).id() == id);
      return this;
    }

    public ContainerRequest build() {
      if (!courseInitialized) {
        throw new RequestException("Course id should be initialized!");
      }

      var predicate = predicates
          .stream()
          .reduce(entry -> true, Predicate::and);

      return new ContainerRequest(predicate);
    }

    private void checkCourseInitialization() {
      if (courseInitialized) {
        throw new RequestException("Course id should not be initialized twice!");
      }
      courseInitialized = true;
    }
  }

}
