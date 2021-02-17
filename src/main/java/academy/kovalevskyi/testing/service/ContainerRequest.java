package academy.kovalevskyi.testing.service;

import academy.kovalevskyi.testing.exception.RequestException;
import academy.kovalevskyi.testing.model.AbstractContainer;
import academy.kovalevskyi.testing.util.ContainerManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Custom request which can filter container classes by course/week/day/id.
 */
public class ContainerRequest implements IRequest {

  private final Predicate<Class<? extends AbstractContainer>> predicate;

  private ContainerRequest(Predicate<Class<? extends AbstractContainer>> predicate) {
    this.predicate = predicate;
  }

  @Override
  public Predicate<Class<? extends AbstractContainer>> getPredicate() {
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

    private String key;
    private boolean week;
    private boolean day;
    private boolean id;
    private final List<Predicate<Class<? extends AbstractContainer>>> predicates;

    private Builder() {
      predicates = new ArrayList<>();
    }

    public Builder course(String key) {
      this.key = key;
      predicates.add(clazz -> ContainerManager.initProvider(clazz).key().equalsIgnoreCase(key));
      return this;
    }

    public Builder week(int number) {
      week = true;
      predicates.add(clazz -> ContainerManager.getAnnotation(clazz).week() == number);
      return this;
    }

    public Builder day(int number) {
      day = true;
      predicates.add(clazz -> ContainerManager.getAnnotation(clazz).day() == number);
      return this;
    }

    public Builder container(int number) {
      id = true;
      predicates.add(clazz -> ContainerManager.getAnnotation(clazz).id() == number);
      return this;
    }

    public ContainerRequest build() {
      if (key == null || key.isBlank()) {
        throw new RequestException("Course key should be provided");
      }
      if (id && (!day || !week)) {
        throw new RequestException("Week and day should be provided to get some container");
      }
      if (day && !week) {
        throw new RequestException("Week should be provided to get containers of some day");
      }

      var predicate = predicates
          .stream()
          .reduce(entry -> true, Predicate::and);

      return new ContainerRequest(predicate);
    }
  }
}
