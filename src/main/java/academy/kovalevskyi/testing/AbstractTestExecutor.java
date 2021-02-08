package academy.kovalevskyi.testing;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import academy.kovalevskyi.testing.view.State;
import academy.kovalevskyi.testing.view.TestHandler;
import java.util.Objects;
import java.util.stream.Stream;
import org.fusesource.jansi.AnsiConsole;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

/**
 * This class allows you to invoke test classes programmatically. All test classes directly or
 * indirectly need to inherit from this class.
 */

@ExtendWith(TestHandler.class)
public abstract class AbstractTestExecutor {

  /**
   * * Launch JUnit 5 and invoke test class programmatically.
   *
   * @throws Exception a lot of reasons
   */
  public void execute() throws Exception {
    final var request = LauncherDiscoveryRequestBuilder
        .request()
        .selectors(selectClass(this.getClass()))
        .build();
    try {
      LauncherFactory.create().execute(request);
    } catch (Exception exception) {
      final var noClassException = Stream
          .iterate(exception, Objects::nonNull, Throwable::getCause)
          .filter(throwable -> throwable.getClass().equals(NoClassDefFoundError.class))
          .findFirst()
          .orElseThrow(() -> exception);
      AnsiConsole.systemInstall();
      System.out.println(TestHandler.prepareReason(State.NO_CLASS, noClassException));
      AnsiConsole.systemUninstall();
    }
  }
}
