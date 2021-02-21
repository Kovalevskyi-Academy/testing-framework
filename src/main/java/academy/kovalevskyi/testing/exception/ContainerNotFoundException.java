package academy.kovalevskyi.testing.exception;

/**
 * Can be thrown if containers are not found.
 */
public class ContainerNotFoundException extends FrameworkException {

  public ContainerNotFoundException() {
  }

  public ContainerNotFoundException(String message) {
    super(message);
  }
}
