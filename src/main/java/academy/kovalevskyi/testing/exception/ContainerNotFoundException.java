package academy.kovalevskyi.testing.exception;

public class ContainerNotFoundException extends FrameworkException {

  public ContainerNotFoundException() {
  }

  public ContainerNotFoundException(String message) {
    super(message);
  }
}
