package academy.kovalevskyi.testing;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import academy.kovalevskyi.testing.view.TestsConsolePrinter;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

@ExtendWith(TestsConsolePrinter.class)
public abstract class AbstractTestExecutor {

  SummaryGeneratingListener listener = new SummaryGeneratingListener();

  /**
   * This method does ... what exactly?
   */
  public void executeTest() {
    LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
        .selectors(selectClass(this.getClass()))
        .build();
    Launcher launcher = LauncherFactory.create();
    launcher.discover(request);
    launcher.registerTestExecutionListeners(listener);
    launcher.execute(request);
  }
}
