package academy.kovalevskyi.testing;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import academy.kovalevskyi.testing.view.TestHandler;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

/**
 * This class allows you to invoke test classes programmatically. All test classes directly or
 * indirectly need to inherit from this class.
 */

@ExtendWith(TestHandler.class)
public abstract class AbstractTestExecutor {

  /**
   * Launch JUnit 5 and invoke test class programmatically.
   */
  public void execute() {
    final var listener = new SummaryGeneratingListener();
    final var request = LauncherDiscoveryRequestBuilder
        .request()
        .selectors(selectClass(this.getClass()))
        .build();
    final var launcher = LauncherFactory.create();
    launcher.discover(request);
    launcher.registerTestExecutionListeners(listener);
    launcher.execute(request);
  }
}
