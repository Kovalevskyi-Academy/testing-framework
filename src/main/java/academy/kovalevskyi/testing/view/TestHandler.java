package academy.kovalevskyi.testing.view;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.AnsiConsole;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

/**
 * This handler makes beautiful console output for JUnit tests.
 */
public class TestHandler implements TestWatcher, BeforeAllCallback, AfterAllCallback,
    BeforeEachCallback, AfterEachCallback {

  private int successful;
  private int failed;
  private int repetition;
  private long beginning;
  private Timer timer;
  private String title;
  private boolean noClassDef;
  private static boolean silentMode;

  /**
   * Enables silent mode to print to console only failed methods.
   *
   * @param silent true is enabled
   */
  public static void setSilentMode(final boolean silent) {
    silentMode = silent;
  }

  /**
   * Provides additional behavior to test containers before all tests are invoked.
   *
   * @param context the current extension context
   */
  @Override
  public void beforeAll(ExtensionContext context) {
    AnsiConsole.systemInstall();
    successful = 0;
    repetition = 0;
    failed = 0;
    beginning = System.currentTimeMillis();
    title = context.getDisplayName();
    if (!silentMode) {
      System.out.println(getHeader());
    }
  }

  /**
   * Provide additional behavior to tests before each test is invoked.
   *
   * @param context the current extension context
   */
  @Override
  public void beforeEach(ExtensionContext context) {
    timer = new Timer(true);
    timer.schedule(createTimer(context), TimeUnit.SECONDS.toMillis(15));
  }

  /**
   * Provide additional behavior to tests after each test is invoked.
   *
   * @param context the current extension context
   */
  @Override
  public void afterEach(ExtensionContext context) {
    timer.cancel();
  }

  /**
   * Invoked after a test has completed successfully.
   *
   * @param context the current extension context
   */
  @Override
  public void testSuccessful(ExtensionContext context) {
    successful++;
    if (!silentMode) {
      System.out.println(getEntry(context, "OK", Color.GREEN));
    }
  }

  /**
   * Invoked after a test has failed.
   *
   * @param context the current extension context
   * @param cause   the throwable that caused test failure
   */
  @Override
  public void testFailed(ExtensionContext context, Throwable cause) {
    if (silentMode && failed == 0) {
      System.out.println(getHeader());
    }
    failed++;
    if (!noClassDef) {
      System.out.println(getEntry(context, cause, "BAD", Color.RED));
    }
  }

  /**
   * Provides additional behavior to test containers after all tests are invoked.
   *
   * @param context the current extension context
   */
  @Override
  public void afterAll(ExtensionContext context) {
    if (!silentMode) {
      System.out.println(underline(prepareResult()));
    } else if (failed == 0) {
      System.out.printf("%s is done successfully in %s!%n",
          context.getDisplayName(),
          prepareDuration());
    } else {
      var result = String.format("%nYou have %d failed method(s), done in %s",
          failed,
          prepareDuration());
      System.out.println(underline(result));
    }
    AnsiConsole.systemUninstall();
  }

  private String prepareResult() {
    final var result = new StringJoiner(" | ");
    result.add(String.format("%nTOTAL %d", successful + failed));
    if (successful > 0) {
      result.add(String.format("SUCCESSFUL %d", successful));
    }
    if (failed > 0) {
      result.add(String.format("FAILED %d", failed));
    }
    return result.add(String.format("TIME %s", prepareDuration())).toString();
  }

  private String prepareDuration() {
    final var duration = System.currentTimeMillis() - beginning;
    final var sec = duration / 1000;
    if (sec > 0) {
      return String.format("%d sec %d ms", sec, duration - (1000 * sec));
    }
    return String.format("%d ms", duration);
  }

  private String underline(final String text) {
    return String.format("%s%n%s", text, "-".repeat(text.length() - 2));
  }

  private String getHeader() {
    return String.format("Result of %s:%n", title);
  }

  private String getEntry(final ExtensionContext context, final String status, final Color color) {
    final var method = String.format("%s()", context.getRequiredTestMethod().getName());
    final var repeatedTest = context.getRequiredTestMethod().getAnnotation(RepeatedTest.class);
    final var result = Ansi.ansi();
    if (Objects.isNull(repeatedTest)) {
      result.format("%s - ", method);
    } else {
      if (repetition == 0) {
        result.format("%s repeated %d times:%n", method, repeatedTest.value());
      }
      result.format("> repetition %d - ", ++repetition);
      if (repeatedTest.value() == repetition) {
        repetition = 0;
      }
    }
    return result.fg(color).a(status).reset().toString();
  }

  private String getEntry(final ExtensionContext context, final Throwable cause,
      final String status, final Color color) {
    if (Objects.equals(cause.getClass(), NoClassDefFoundError.class)) {
      noClassDef = true;
      return Ansi
          .ansi()
          .fg(color)
          .format("Zeus can not find '%s'%n", cause.getMessage())
          .format("Reasons:%n")
          .format("- your jar file is absent in the classpath%n")
          .format("- class is absent in your jar file%n")
          .a("- structure of the project is not default")
          .reset()
          .toString();
    }
    final var result = new StringJoiner(System.lineSeparator());
    result.add(getEntry(context, status, color));
    final var message = Ansi.ansi().fg(color);
    if (Objects.equals(cause.getClass(), NoSuchMethodError.class)) {
      message.format("%s is absent in your jar file, write the method", cause.getMessage());
    } else {
      message.a(prepareStacktrace(cause));
    }
    result.add(message.reset().toString());
    return result.toString();
  }

  private String prepareStacktrace(final Throwable cause) {
    final var stacktrace = new ByteArrayOutputStream();
    cause.printStackTrace(new PrintStream(stacktrace));
    return stacktrace.toString().trim();
  }

  private TimerTask createTimer(final ExtensionContext context) {
    return new TimerTask() {

      @Override
      public void run() {
        var error = "Looks like an infinity loop or your method is so slowly..";
        var message = getEntry(context, new TimeoutException(error), "INTERRUPTED", Color.RED);
        System.out.println(message);
        System.exit(0);
      }
    };
  }
}
