package academy.kovalevskyi.testing.exception;

import academy.kovalevskyi.testing.service.ContainerRequest;

/**
 * Can be thrown if {@link ContainerRequest} has build errors.
 */
public class RequestException extends FrameworkException {

  public RequestException() {
  }

  public RequestException(String message) {
    super(message);
  }
}
