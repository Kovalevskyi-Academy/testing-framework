package academy.kovalevskyi.testing.service;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;
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
public class ContainerHandler implements TestWatcher, BeforeAllCallback, AfterAllCallback,
    BeforeEachCallback, AfterEachCallback, ExecutionCondition {

  static {
    System.setProperty("com.google.common.truth.disable_stack_trace_cleaning", "true");
  }

  private int successful = 0;
  private int failed = 0;
  private int aborted = 0;
  private int disabled = 0;
  private int repeatedTestInvocations = 0;
  private int failedRepeatedTestInvocations = 0;
  private long time = 0;
  private long beginning;
  private Timer timer;
  private String containerName;
  private String testName;
  private String repeatedTestSummary;
  private boolean repeatedTest;
  private boolean abortedRepeatedTest;
  private boolean noClassDef;
  private boolean errorMode;
  private final PrintStream defaultStdout;
  private final PrintStream defaultStderr;
  private final PrintStream gagPrintStream;
  private final boolean debugMode;

  public ContainerHandler() {
    errorMode = Boolean.parseBoolean(System.getProperty(FrameworkProperty.ERROR_MODE));
    debugMode = Boolean.parseBoolean(System.getProperty(FrameworkProperty.DEBUG_MODE));
    gagPrintStream = new PrintStream(OutputStream.nullOutputStream());
    defaultStdout = System.out;
    defaultStderr = System.err;
  }

  /**
   * Provides an Optional value of any {@link Throwable} instance from chain of throwable.
   *
   * @param chain     chain of throwable
   * @param exception any heir class of {@link Throwable}
   * @return an Optional of instance of class or an empty Optional
   */
  public static Optional<? extends Throwable> getExceptionFromThrowableChain(
      final Throwable chain,
      final Class<? extends Throwable> exception) {
    return Stream
        .iterate(chain, Objects::nonNull, Throwable::getCause)
        .dropWhile(throwable -> !throwable.getClass().equals(exception))
        .findFirst();
  }

  /**
   * Provides an error message of {@link NoClassDefFoundError}.
   *
   * @param error {@link NoClassDefFoundError} instance
   * @return prepared error message
   */
  public static String getReason(final NoClassDefFoundError error) {
    final var result = new StringJoiner("\n");
    result.add(String.format("\rZeus can not find '%s'", error.getMessage()));
    result.add("Reasons:");
    result.add("- your jar file is absent in the classpath");
    result.add("- class is absent in your jar file");
    result.add("- structure of the project is not default");
    return result.toString();
  }

  /**
   * Provides additional behavior to test container before all tests are invoked.
   *
   * @param context the current extension context
   */
  @Override
  public void beforeAll(ExtensionContext context) {
    AnsiConsole.systemInstall();
    if (debugMode) {
      errorMode = false;
    } else {
      System.setOut(gagPrintStream);
    }
    System.setErr(gagPrintStream);
    containerName = context.getDisplayName();
    if (!errorMode) {
      printHeader();
    }
  }

  /**
   * Evaluates to determine if a given container or test should be executed based on the supplied
   * {@link ExtensionContext}. Provides additional behavior to each entry in the test container
   * before it is invoked.
   *
   * @param context the current extension context
   * @return factory for creating enabled results
   */
  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    final var entryUniqueId = context.getUniqueId();
    if (entryUniqueId.matches("^.+\\[method:.+\\)]$")) {
      testName = prepareTestName(context);
      printSummary();
      repeatedTest = false;
    } else if (entryUniqueId.matches("^.+\\[test-template:.+\\)]$")) {
      testName = prepareTestName(context);
      printSummary();
      repeatedTest = true;
      abortedRepeatedTest = false;
      repeatedTestInvocations = 0;
      failedRepeatedTestInvocations = 0;
    } else if (entryUniqueId.matches("^.+\\[test-template-invocation:#\\d+]$")) {
      repeatedTestInvocations++;
    }
    return ConditionEvaluationResult.enabled("For printing result of test to console");
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
    printEntry(State.RUNNING);
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
   * Invoked after a test has been disabled.
   *
   * @param context the current extension context
   * @param reason  the reason that test was disabled
   */
  @Override
  public void testDisabled(ExtensionContext context, Optional<String> reason) {
    disabled++;
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
    if (getExceptionFromThrowableChain(cause, NoClassDefFoundError.class).isPresent()) {
      printEntry(State.NO_CLASS, cause);
      noClassDef = true;
    } else if (getExceptionFromThrowableChain(cause, NoSuchMethodError.class).isPresent()) {
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
      defaultStdout.println(underline(prepareFooter()));
    } else if (errors == 0) {
      defaultStdout.printf(
          "\r%s is done successfully in %s!%n",
          context.getDisplayName(),
          prepareDuration());
    }
    AnsiConsole.systemUninstall();
    if (!debugMode) {
      System.setOut(defaultStdout);
    }
    System.setErr(defaultStderr);
    gagPrintStream.close();
  }

  private void printHeader() {
    defaultStdout.printf("\rResult of %s:%n%n", containerName);
  }

  private void printEntry(final State state) {
    if (!noClassDef) {
      if (errorMode) {
        var errors = failed + aborted;
        if (errors == 1 && (state != State.SUCCESSFUL && state != State.RUNNING)) {
          printHeader();
        }
        if (errors == 0) {
          defaultStdout.printf("\r%s -> %s", containerName, testName);
        } else {
          defaultStdout.printf("\r%s", testName);
        }
      } else {
        defaultStdout.printf("\r%s", testName);
      }
      if (repeatedTest) {
        repeatedTestSummary = prepareSummary(
            testName,
            state,
            failedRepeatedTestInvocations,
            repeatedTestInvocations);
        defaultStdout.printf(" test %d", repeatedTestInvocations);
      }
      var status = prepareStatus(state);
      if (state == State.RUNNING) {
        defaultStdout.print(status);
        if (debugMode) {
          defaultStdout.printf("%n");
        }
      } else if (state == State.FAILED || state == State.INTERRUPTED || (!repeatedTest
          && (state == State.ABORTED || state == State.NO_METHOD
          || (!errorMode && state == State.SUCCESSFUL)))) {
        defaultStdout.println(status);
      }
    }
  }

  private void printEntry(final State state, final Throwable cause) {
    if (!noClassDef) {
      printEntry(state);
      if (repeatedTest && (state == State.NO_METHOD || state == State.ABORTED)) {
        repeatedTestSummary += String.format("%n%s", prepareReason(state, cause));
      } else {
        defaultStdout.println(prepareReason(state, cause));
      }
    }
  }

  private void printSummary() {
    if (!noClassDef) {
      final var successful = repeatedTestInvocations - failedRepeatedTestInvocations;
      if (repeatedTest && successful != 0 && (!errorMode || abortedRepeatedTest)) {
        defaultStdout.println(repeatedTestSummary);
      }
    }
  }

  private String prepareTestName(final ExtensionContext context) {
    final var name = context.getDisplayName();
    final var match = name.matches("^.+\\(.*$");
    return String.format("%s()", match ? name.substring(0, name.indexOf('(')) : name);
  }

  private String prepareFooter() {
    final var result = new StringJoiner(" | ");
    result.add(String.format("\r %nTOTAL %d", successful + failed + aborted + disabled));
    if (successful > 0) {
      result.add(String.format("SUCCESSFUL %d", successful));
    }
    if (failed > 0) {
      result.add(String.format("FAILED %d", failed));
    }
    if (aborted > 0) {
      result.add(String.format("ABORTED %d", aborted));
    }
    if (disabled > 0) {
      result.add(String.format("DISABLED %d", disabled));
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

  private String prepareReason(final State state, final Throwable cause) {
    final var result = Ansi.ansi().fg(state.color);
    if (state == State.NO_CLASS) {
      result.a(getReason((NoClassDefFoundError) cause));
    } else if (state == State.NO_METHOD) {
      result.format("%s%n", cause.getMessage().trim());
      result.format("Reasons:%n");
      result.format("- method is absent%n");
      result.format("- signature of method is different");
    } else if (state == State.INTERRUPTED) {
      result.format("Time is out! Something went wrong...%n");
    } else if (cause instanceof AssertionError || cause instanceof TestAbortedException) {
      var message = cause.getMessage();
      if (message != null && !message.isBlank()) {
        result.a(message.trim());
      } else {
        result.a("Error message is not provided");
      }
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
        printEntry(State.INTERRUPTED, new TimeoutException());
        System.exit(0);
      }
    };
  }
}
