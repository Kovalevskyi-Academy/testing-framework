package academy.kovalevskyi.testing.exception;

public class NotAnnotatedContainerException extends FrameworkException {

  public NotAnnotatedContainerException() {
  }

  public NotAnnotatedContainerException(String message) {
    super(message);
  }
}
