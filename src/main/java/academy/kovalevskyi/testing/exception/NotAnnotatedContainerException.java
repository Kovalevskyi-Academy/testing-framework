package academy.kovalevskyi.testing.exception;

import academy.kovalevskyi.testing.annotation.Container;

/**
 * Can be thrown if some class is not annotated with {@link Container}.
 */
public class NotAnnotatedContainerException extends FrameworkException {

  public NotAnnotatedContainerException() {
  }

  public NotAnnotatedContainerException(String message) {
    super(message);
  }
}
