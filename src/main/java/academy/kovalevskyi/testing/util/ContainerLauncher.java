package academy.kovalevskyi.testing.util;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import academy.kovalevskyi.testing.annotation.Container;
import academy.kovalevskyi.testing.exception.ContainerNotFoundException;
import academy.kovalevskyi.testing.exception.NotAnnotatedContainerException;
import academy.kovalevskyi.testing.service.ContainerHandler;
import academy.kovalevskyi.testing.service.FrameworkProperty;
import academy.kovalevskyi.testing.service.State;
import java.util.List;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

/**
 * Launches test containers.
 */
public final class ContainerLauncher {

  /**
   * Launches test containers programmatically with JUnit engine.
   *
   * @param containers  list of test classes
   * @param errorMode   enable/disable error mode
   * @param debugMode   enable/disable debug mode
   * @param verboseMode enable/disable verbose mode
   */
  public static void execute(
      final List<Class<?>> containers,
      final boolean errorMode,
      final boolean debugMode,
      final boolean verboseMode) {
    if (containers.isEmpty()) {
      throw new ContainerNotFoundException("No containers to execute");
    }

    for (var container : containers) {
      execute(container, errorMode, debugMode, verboseMode);
    }
  }

  /**
   * Launches test container programmatically with JUnit engine.
   *
   * @param container   test class
   * @param errorMode   enable/disable error mode
   * @param debugMode   enable/disable debug mode
   * @param verboseMode enable/disable verbose mode
   */
  public static void execute(
      final Class<?> container,
      final boolean errorMode,
      final boolean debugMode,
      final boolean verboseMode) {
    if (!container.isAnnotationPresent(Container.class)) {
      throw new NotAnnotatedContainerException(
          String.format("Unsupported class %s", container.getName()));
    }

    System.setProperty(FrameworkProperty.ERROR_MODE, String.valueOf(errorMode));
    System.setProperty(FrameworkProperty.DEBUG_MODE, String.valueOf(debugMode));
    System.setProperty(FrameworkProperty.VERBOSE_MODE, String.valueOf(verboseMode));
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
}
