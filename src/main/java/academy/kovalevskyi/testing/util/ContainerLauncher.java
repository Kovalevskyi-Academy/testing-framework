package academy.kovalevskyi.testing.util;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import academy.kovalevskyi.testing.exception.ContainerNotFoundException;
import academy.kovalevskyi.testing.service.ContainerHandler;
import academy.kovalevskyi.testing.service.ContainerRequest;
import academy.kovalevskyi.testing.service.FrameworkProperty;
import academy.kovalevskyi.testing.service.State;
import java.util.List;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

public class ContainerLauncher {

  /**
   * Launches test container programmatically with JUnit engine.
   *
   * @param errorMode show only unsuccessful methods
   * @param container test class
   */
  public static void execute(final boolean errorMode, final Class<?> container) {
    setErrorMode(errorMode);
    execute(container);
  }

  /**
   * Launches test containers programmatically with JUnit engine.
   *
   * @param errorMode  show only unsuccessful methods
   * @param containers list of test classes
   */
  public static void execute(final boolean errorMode, final List<Class<?>> containers) {
    if (containers.isEmpty()) {
      throw new ContainerNotFoundException("No containers to execute");
    }
    setErrorMode(errorMode);
    for (var container : containers) {
      execute(container);
    }
  }

  /**
   * Launches test containers programmatically with JUnit engine.
   *
   * @param errorMode show only unsuccessful methods
   * @param request   combined request of containers
   */
  public static void execute(final boolean errorMode, final ContainerRequest request) {
    execute(errorMode, ContainerManager.getContainers(request));
  }

  private static void execute(final Class<?> container) {
    final var request = LauncherDiscoveryRequestBuilder
        .request()
        .selectors(selectClass(container))
        .build();
    try {
      LauncherFactory.create().execute(request);
    } catch (JUnitException exception) {
      final var noClassException =
          ContainerHandler.getExceptionFromThrowableChain(exception, NoClassDefFoundError.class);
      if (noClassException.isPresent()) {
        AnsiConsole.systemInstall();
        var message = Ansi
            .ansi()
            .fg(State.NO_CLASS.color)
            .a(ContainerHandler.getReason((NoClassDefFoundError) noClassException.get()))
            .reset()
            .toString();
        System.out.println(message);
        System.out.println("-----------------------------------------");
        AnsiConsole.systemUninstall();
      } else {
        throw exception;
      }
    }
  }

  private static void setErrorMode(boolean errorMode) {
    System.setProperty(FrameworkProperty.ERROR_MODE, String.valueOf(errorMode));
  }
}
