package academy.kovalevskyi.testing.util;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import academy.kovalevskyi.testing.exception.ContainerNotFoundException;
import academy.kovalevskyi.testing.model.AbstractContainer;
import academy.kovalevskyi.testing.service.ContainerHandler;
import academy.kovalevskyi.testing.service.ContainerRequest;
import academy.kovalevskyi.testing.service.IPropertyManager;
import academy.kovalevskyi.testing.service.State;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

public class ContainerLauncher {

  /**
   * Launches test container programmatically with JUnit engine.
   *
   * @param errorMode show only unsuccessful methods
   * @param container any heir of AbstractContainer class
   * @throws Exception a lot of reasons
   */
  public static void execute(
      final boolean errorMode,
      final Class<? extends AbstractContainer> container) throws Exception {
    System.setProperty(IPropertyManager.ERROR_MODE, String.valueOf(errorMode));
    execute(container);
  }

  /**
   * Launches test containers programmatically with JUnit engine.
   *
   * @param errorMode  show only unsuccessful methods
   * @param containers list of any heir of AbstractContainer class
   * @throws Exception a lot of reasons
   */
  public static void execute(
      final boolean errorMode,
      final List<Class<? extends AbstractContainer>> containers) throws Exception {
    if (containers.isEmpty()) {
      throw new ContainerNotFoundException("No containers to execute");
    }
    System.setProperty(IPropertyManager.ERROR_MODE, String.valueOf(errorMode));
    for (var container : containers) {
      execute(container);
    }
  }

  /**
   * Launches test containers programmatically with JUnit engine.
   *
   * @param errorMode show only unsuccessful methods
   * @param request   combined request of containers
   * @throws Exception a lot of reasons
   */
  public static void execute(final boolean errorMode, final ContainerRequest request)
      throws Exception {
    execute(errorMode, ContainerManager.getContainers(request));
  }

  private static void execute(final Class<? extends AbstractContainer> container) throws Exception {
    final var request = LauncherDiscoveryRequestBuilder
        .request()
        .selectors(selectClass(container))
        .build();
    try {
      LauncherFactory.create().execute(request);
    } catch (Exception exception) {
      final var noClassException = (NoClassDefFoundError) Stream
          .iterate(exception, Objects::nonNull, Throwable::getCause)
          .filter(throwable -> throwable.getClass().equals(NoClassDefFoundError.class))
          .findFirst()
          .orElseThrow(() -> exception);
      AnsiConsole.systemInstall();
      var message = Ansi
          .ansi()
          .fg(State.NO_CLASS.color)
          .a(ContainerHandler.getReason(noClassException))
          .reset()
          .toString();
      System.out.println(message);
      System.out.println("-----------------------------------------");
      AnsiConsole.systemUninstall();
    }
  }
}
