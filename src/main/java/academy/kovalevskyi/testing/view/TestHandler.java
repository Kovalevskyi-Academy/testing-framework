package academy.kovalevskyi.testing.view;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.StringJoiner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.opentest4j.TestAbortedException;

/**
 * This handler makes beautiful console output for JUnit tests.
 */
public class TestHandler implements TestWatcher, BeforeAllCallback, AfterAllCallback,
    BeforeEachCallback, AfterEachCallback, ExecutionCondition {

  private int successful = 0;
  private int failed = 0;
  private int aborted = 0;
  private int repeatedTestInvocations = 0;
  private int failedRepeatedTestInvocations = 0;
  private long time = 0;
  private long beginning;
  private Timer timer;
  private String containerName;
  private String testName;
  private String repeatedTestSummary;
  private PrintStream defaultPrintStream;
  private PrintStream defaultErrorPrintStream;
  private PrintStream gagPrintStream;
  private boolean repeatedTest;
  private boolean abortedRepeatedTest;
  private boolean noClassDef;
  private static boolean errorMode;

  /**
   * Enables error mode to print to console only failed methods.
   *
   * @param flag true is enabled
   */
  public static void setErrorMode(final boolean flag) {
    errorMode = flag;
  }

  /**
   * Provides additional behavior to test container before all tests are invoked.
   *
   * @param context the current extension context
   */
  @Override
  public void beforeAll(ExtensionContext context) {
    AnsiConsole.systemInstall();
    containerName = context.getDisplayName();
    defaultPrintStream = System.out;
    defaultErrorPrintStream = System.err;
    gagPrintStream = new PrintStream(OutputStream.nullOutputStream());
    if (!errorMode) {
      printHeader();
    }
  }

  /**
   * Evaluates to determine if a given container or test should be executed based on the supplied
   * ExtensionContext. Provides additional behavior to each entry in the test container before it is
   * invoked.
   *
   * @param context the current extension context
   * @return factory for creating enabled results
   */
  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    final var entryUniqueId = context.getUniqueId();
    if (entryUniqueId.matches("^.*\\[method:.*\\)]$")) {
      printSummary();
      repeatedTest = false;
    } else if (entryUniqueId.matches("^.*\\[test-template:.*\\)]$")) {
      printSummary();
      repeatedTest = true;
      abortedRepeatedTest = false;
      repeatedTestInvocations = 0;
      failedRepeatedTestInvocations = 0;
    } else if (entryUniqueId.matches("^.*\\[test-template-invocation:#\\d*]$")) {
      repeatedTestInvocations++;
    }
    return ConditionEvaluationResult.enabled("For printing results of tests to console");
  }

  /**
   * Provide additional behavior to tests before each test is invoked.
   *
   * @param context the current extension context
   */
  @Override
  public void beforeEach(ExtensionContext context) {
    timer = new Timer(true);
    timer.schedule(createTimer(), 15_000);
    testName = String.format("%s()", context.getRequiredTestMethod().getName());
    printEntry(State.RUNNING);
    disableConsolePrints();
    beginning = System.currentTimeMillis();
  }

  /**
   * Provide additional behavior to tests after each test is invoked.
   *
   * @param context the current extension context
   */
  @Override
  public void afterEach(ExtensionContext context) {
    time += System.currentTimeMillis() - beginning;
    timer.cancel();
    enableConsolePrints();
  }

  /**
   * Invoked after a test has completed successfully.
   *
   * @param context the current extension context
   */
  @Override
  public void testSuccessful(ExtensionContext context) {
    successful++;
    printEntry(State.SUCCESSFUL);
  }

  /**
   * Invoked after a test has been aborted.
   *
   * @param context the current extension context
   * @param cause   the throwable that caused test failure
   */
  @Override
  public void testAborted(ExtensionContext context, Throwable cause) {
    aborted++;
    if (repeatedTest) {
      abortedRepeatedTest = true;
    }
    printEntry(State.ABORTED, cause);
  }

  /**
   * Invoked after a test has failed.
   *
   * @param context the current extension context
   * @param cause   the throwable that caused test failure
   */
  @Override
  public void testFailed(ExtensionContext context, Throwable cause) {
    failed++;
    if (repeatedTest) {
      failedRepeatedTestInvocations++;
    }
    final var clazz = cause.getClass();
    if (clazz.equals(NoClassDefFoundError.class)) {
      printEntry(State.NO_CLASS, cause);
      noClassDef = true;
    } else if (clazz.equals(NoSuchMethodError.class)) {
      printEntry(State.NO_METHOD, cause);
    } else {
      printEntry(State.FAILED, cause);
    }
  }

  /**
   * Provides additional behavior to test containers after all tests are invoked.
   *
   * @param context the current extension context
   */
  @Override
  public void afterAll(ExtensionContext context) {
    printSummary();
    final var errors = failed + aborted;
    if (!errorMode || errors > 0) {
      System.out.println(underline(prepareFooter()));
    } else if (errors == 0) {
      System.out.printf(
          "\r%s is done successfully in %s!%n",
          context.getDisplayName(),
          prepareDuration());
    }
    AnsiConsole.systemUninstall();
  }

  private void enableConsolePrints() {
    System.setOut(defaultPrintStream);
    System.setErr(defaultErrorPrintStream);
  }

  private void disableConsolePrints() {
    System.setOut(gagPrintStream);
    System.setErr(gagPrintStream);
  }

  private void printHeader() {
    System.out.printf("\rResult of %s:%n%n", containerName);
  }

  private void printEntry(final State state) {
    if (!noClassDef) {
      if (errorMode) {
        var errors = failed + aborted;
        if (errors == 1 && (state != State.SUCCESSFUL && state != State.RUNNING)) {
          printHeader();
        }
        if (errors == 0) {
          System.out.printf("\r%s -> %s", containerName, testName);
        } else {
          System.out.printf("\r%s", testName);
        }
      } else {
        System.out.printf("\r%s", testName);
      }
      if (repeatedTest) {
        repeatedTestSummary = prepareSummary(
            testName,
            state,
            failedRepeatedTestInvocations,
            repeatedTestInvocations);
        System.out.printf(" test %d", repeatedTestInvocations);
      }
      var status = prepareStatus(state);
      if (state == State.RUNNING) {
        System.out.print(status);
      } else if (state == State.FAILED || state == State.INTERRUPTED || (!repeatedTest
          && (state == State.ABORTED || state == State.NO_METHOD
          || (!errorMode && state == State.SUCCESSFUL)))) {
        System.out.println(status);
      }
    }
  }

  private void printEntry(final State state, final Throwable cause) {
    if (!noClassDef) {
      printEntry(state);
      if (repeatedTest && (state == State.NO_METHOD || state == State.ABORTED)) {
        repeatedTestSummary += String.format("%n%s", prepareReason(state, cause));
      } else {
        System.out.println(prepareReason(state, cause));
      }
    }
  }

  private void printSummary() {
    if (!noClassDef) {
      final var successful = repeatedTestInvocations - failedRepeatedTestInvocations;
      if (repeatedTest && successful != 0 && (!errorMode || abortedRepeatedTest)) {
        System.out.println(repeatedTestSummary);
      }
    }
  }

  private String prepareFooter() {
    final var result = new StringJoiner(" | ");
    result.add(String.format("\r %nTOTAL %d", successful + failed + aborted));
    if (successful > 0) {
      result.add(String.format("SUCCESSFUL %d", successful));
    }
    if (failed > 0) {
      result.add(String.format("FAILED %d", failed));
    }
    if (aborted > 0) {
      result.add(String.format("ABORTED %d", aborted));
    }
    return result.add(String.format("TIME %s", prepareDuration())).toString();
  }

  private String prepareSummary(
      final String name,
      final State state,
      final int failed,
      final int total) {
    final var result = new StringBuilder().append('\r').append(name);
    final var successful = total - failed;
    if (total > 1 && (failed == 0 || successful == 0)) {
      result.append(String.format(" %d tests", total));
    }
    if (state != State.SUCCESSFUL && state != State.FAILED) {
      result.append(prepareStatus(state));
    } else if (failed == 0) {
      result.append(prepareStatus(State.SUCCESSFUL));
    } else if (successful == 0) {
      result.append(prepareStatus(State.FAILED));
    } else {
      var suffix = successful > 1 ? "s" : "";
      result.append(String.format(" %d test%s of %d", successful, suffix, total));
      result.append(prepareStatus(State.SUCCESSFUL));
    }
    return result.toString();
  }

  private String prepareStatus(final State state) {
    return Ansi.ansi().a(" - ").fg(state.color).a(state.status).reset().toString();
  }

  private String prepareDuration() {
    final var sec = time / 1000;
    if (sec > 0) {
      return String.format("%d sec %d ms", sec, time - (1000 * sec));
    }
    return String.format("%d ms", time);
  }

  private String underline(final String text) {
    return String.format("%s%n%s", text, "-".repeat(text.trim().length()));
  }

  public static String prepareReason(final State state, final Throwable cause) {
    final var result = Ansi.ansi().fg(state.color);
    if (cause instanceof AssertionError || cause.getClass().equals(TestAbortedException.class)) {
      result.a(cause.getMessage().trim());
    } else if (state == State.NO_CLASS) {
      result
          .format("\rZeus can not find '%s'%n", cause.getMessage())
          .format("Reasons:%n")
          .format("- your jar file is absent in the classpath%n")
          .format("- class is absent in your jar file%n")
          .a("- structure of the project is not default");
    } else if (state == State.NO_METHOD) {
      result.format("%s is absent in your class!", cause.getMessage());
    } else if (state == State.INTERRUPTED) {
      result.a("Time is out! Looks like an infinity loop or your method is so slowly..");
    } else {
      result.format("Thrown unexpected %s", cause.getClass().getName());
      var message = cause.getMessage();
      if (message != null) {
        result.format(": %s", message.trim());
      }
      result.format("%nat %s", cause.getStackTrace()[0].toString());
    }
    return result.reset().toString();
  }

  private TimerTask createTimer() {
    return new TimerTask() {

      @Override
      public void run() {
        enableConsolePrints();
        printEntry(State.INTERRUPTED, new TimeoutException());
        System.exit(0);
      }
    };
  }
}
