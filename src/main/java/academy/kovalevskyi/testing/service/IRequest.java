package academy.kovalevskyi.testing.service;

import academy.kovalevskyi.testing.AbstractTestExecutor;
import java.util.function.Predicate;

/**
 * Services to filter container classes.
 */
public interface IRequest {

  Predicate<Class<? extends AbstractTestExecutor>> getPredicate();
}
