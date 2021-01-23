package academy.kovalevskyi.testing;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import academy.kovalevskyi.testing.view.TestsConsolePrinter;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;


/**
 * This class allows you to apply some settings and parameters globally for all tests.
 * All tests directly or indirectly need to inherit from this class.
 *
 * @Timeout is used to define a global timeout for all tested classes.
 * @ExtendWith — ??.
 */
@ExtendWith(TestsConsolePrinter.class)
@Timeout(value = 10, unit = TimeUnit.SECONDS)
public abstract class AbstractTestExecutor {

  private final SummaryGeneratingListener listener = new SummaryGeneratingListener();


  /**
   * Execute test class programmatically.
   */
  public void executeTest() {
    var request = LauncherDiscoveryRequestBuilder
        .request()
        .selectors(selectClass(this.getClass()))
        .build();
    Launcher launcher = LauncherFactory.create();
    launcher.discover(request);
    launcher.registerTestExecutionListeners(listener);
    launcher.execute(request);
  }
}
