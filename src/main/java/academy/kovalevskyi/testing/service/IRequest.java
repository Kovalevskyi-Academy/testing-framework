package academy.kovalevskyi.testing.service;

import java.util.function.Predicate;

/**
 * Services to filter test classes.
 */
public interface IRequest {

  Predicate<Class<?>> getPredicate();
}
