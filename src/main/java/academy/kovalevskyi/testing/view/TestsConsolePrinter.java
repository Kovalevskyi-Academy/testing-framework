package academy.kovalevskyi.testing.view;

import academy.kovalevskyi.testing.AbstractTestExecutor;
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
 * This class is called from the parallel universe for beautiful display of test results called from
 * java code. NEED MORE DETAILS
 */
public class TestsConsolePrinter implements TestWatcher, BeforeAllCallback, AfterAllCallback,
    BeforeEachCallback, AfterEachCallback {


  private int successful = 0;
  private int failed = 0;
  private int repetition = 0;
  private long time;
  private Timer timer;
  private String className;
  private boolean noClassDef;
  private static boolean silentMode;

  public static void setSilentMode(boolean silent) {
    silentMode = silent;
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    AnsiConsole.systemInstall();
    time = System.nanoTime();
    className = context.getDisplayName();
    if (!silentMode) {
      System.out.println(getHeader());
    }
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    timer = new Timer(true);
    timer.schedule(
        new TimerTask() {

          @Override
          public void run() {
            var error = "Looks like an infinity loop or your method is so slowly..";
            var message = getEntry(context, new TimeoutException(error), "ABORTED", Color.RED);
            System.out.println(message);
            System.exit(-1);
          }
        },
        TimeUnit.SECONDS.toMillis(AbstractTestExecutor.TEST_TIMEOUT_SEC));
  }

  @Override
  public void afterEach(ExtensionContext context) {
    timer.cancel();
  }

  @Override
  public void testSuccessful(ExtensionContext context) {
    successful++;
    if (!silentMode) {
      System.out.println(getEntry(context, "OK", Color.GREEN));
    }
  }

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

  @Override
  public void afterAll(ExtensionContext context) {
    var ms = calculateDurationMillisTime(time);
    if (!silentMode) {
      var result = String.format("%nTOTAL %d | SUCCESSFUL %d | FAILED %d | TIME %d ms%n",
          successful + failed,
          successful,
          failed,
          ms);
      System.out.printf("%s%s%n", result, "-".repeat(result.length() - 4));
    } else if (failed == 0) {
      System.out.printf("%s is done successfully in %d ms!%n", context.getDisplayName(), ms);
    } else {
      var result = String.format("%nYou have %d failed method(s), done in %d ms%n", failed, ms);
      System.out.printf("%s%s%n", result, "-".repeat(result.length() - 4));
    }
    AnsiConsole.systemUninstall();
  }

  private long calculateDurationMillisTime(long beginNanos) {
    if (System.nanoTime() < beginNanos) {
      throw new IllegalArgumentException("End time less start time");
    }
    return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - beginNanos);
  }

  private String getHeader() {
    return String.format("Result of %s:%n", className);
  }

  private String getEntry(ExtensionContext context, String status, Color color) {
    var method = String.format("%s()", context.getRequiredTestMethod().getName());
    var repeatedTest = context.getRequiredTestMethod().getAnnotation(RepeatedTest.class);
    var result = Ansi.ansi();
    if (Objects.isNull(repeatedTest)) {
      result.format("%s - ", method);
    } else {
      if (repetition == 0) {
        result.format("%s repeated %d times:%n", method, repeatedTest.value());
      }
      result.format("• repetition %d - ", ++repetition);
      if (repeatedTest.value() == repetition) {
        repetition = 0;
      }
    }
    result.fg(color).a(status).reset();
    return result.toString();
  }

  private String getEntry(ExtensionContext context, Throwable cause, String status, Color color) {
    if (Objects.equals(cause.getClass(), NoClassDefFoundError.class)) {
      noClassDef = true;
      return Ansi
          .ansi()
          .fg(color)
          .format("Zeus can not find '%s'%n", cause.getMessage())
          .a("Reasons:")
          .newline()
          .a("- your jar file is absent in the classpath")
          .newline()
          .a("- class is absent in your jar file")
          .newline()
          .a("- structure of the project is not default")
          .reset()
          .toString();
    }
    var result = new StringJoiner(System.lineSeparator());
    result.add(getEntry(context, status, color));
    var message = Ansi.ansi().fg(color);
    if (Objects.equals(cause.getClass(), NoSuchMethodError.class)) {
      message.format("%s is absent in your jar file, write the method", cause.getMessage());
    } else {
      message.a(getReasonOrStacktrace(cause));
    }
    result.add(message.reset().toString());
    return result.toString();
  }

  private String getReasonOrStacktrace(Throwable cause) {
    var reason = cause.getMessage();
    if (Objects.isNull(reason)) {
      var stacktrace = new ByteArrayOutputStream();
      cause.printStackTrace(new PrintStream(stacktrace));
      return stacktrace.toString().trim();
    }
    return reason;
  }
}
