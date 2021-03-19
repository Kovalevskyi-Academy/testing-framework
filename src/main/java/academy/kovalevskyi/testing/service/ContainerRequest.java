package academy.kovalevskyi.testing.service;

import academy.kovalevskyi.testing.exception.RequestException;
import academy.kovalevskyi.testing.util.ContainerManager;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Predicate;

/**
 * Custom request which can filter container classes by course/week/day/id.
 */
public class ContainerRequest implements Request {

  private final String key;
  private final int week;
  private final int day;
  private final int id;
  private final Predicate<Class<?>> predicate;

  private ContainerRequest(String key, int week, int day, int id, Predicate<Class<?>> predicate) {
    this.key = key;
    this.week = week;
    this.day = day;
    this.id = id;
    this.predicate = predicate;
  }

  @Override
  public Predicate<Class<?>> getPredicate() {
    return predicate;
  }

  @Override
  public String toString() {
    var message = new StringJoiner(", ", "[", "]");
    if (key != null) {
      message.add(String.format("key - %s", key));
    }
    if (week >= 0) {
      message.add(String.format("week - %d", week));
    }
    if (day >= 0) {
      message.add(String.format("day - %d", day));
    }
    if (id >= 0) {
      message.add(String.format("id - %d", id));
    }
    return message.toString();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String key;
    private int week;
    private int day;
    private int id;
    private final List<Predicate<Class<?>>> predicates;

    private Builder() {
      week = Integer.MIN_VALUE;
      day = Integer.MIN_VALUE;
      id = Integer.MIN_VALUE;
      predicates = new ArrayList<>();
    }

    public Builder course(String key) {
      this.key = key;
      predicates.add(clazz -> ContainerManager.initProvider(clazz).key().equalsIgnoreCase(key));
      return this;
    }

    public Builder week(int number) {
      week = number;
      predicates.add(clazz -> ContainerManager.getAnnotation(clazz).week() == number);
      return this;
    }

    public Builder day(int number) {
      day = number;
      predicates.add(clazz -> ContainerManager.getAnnotation(clazz).day() == number);
      return this;
    }

    public Builder container(int number) {
      id = number;
      predicates.add(clazz -> ContainerManager.getAnnotation(clazz).id() == number);
      return this;
    }

    /**
     * Prepares request.
     *
     * @return instance of {@link ContainerRequest}
     * @throws RequestException if request has some build errors
     */
    public ContainerRequest build() {
      if (key == null || key.isBlank()) {
        throw new RequestException("Course key should be provided");
      }
      if (id >= 0 && (day < 0 || week < 0)) {
        throw new RequestException("Week and day should be provided to get some container");
      }
      if (day >= 0 && week < 0) {
        throw new RequestException("Week should be provided to get containers of some day");
      }

      var predicate = predicates.stream().reduce(entry -> true, Predicate::and);
      return new ContainerRequest(key, week, day, id, predicate);
    }
  }
}
