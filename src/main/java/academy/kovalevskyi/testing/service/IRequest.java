package academy.kovalevskyi.testing.service;

import academy.kovalevskyi.testing.model.AbstractContainer;
import java.util.function.Predicate;

/**
 * Services to filter container classes.
 */
public interface IRequest {

  Predicate<Class<? extends AbstractContainer>> getPredicate();
}
