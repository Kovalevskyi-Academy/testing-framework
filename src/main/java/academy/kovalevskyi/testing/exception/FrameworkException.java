package academy.kovalevskyi.testing.exception;


/**
 * Base Testing Framework exception. All exception classes directly or indirectly need to inherit
 * from this class.
 */
public abstract class FrameworkException extends RuntimeException {

  public FrameworkException() {
  }

  public FrameworkException(String message) {
    super(message);
  }
}
