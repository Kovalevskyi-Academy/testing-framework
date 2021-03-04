package academy.kovalevskyi.testing.service;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
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

  {
    System.setProperty("com.google.common.truth.disable_stack_trace_cleaning", "true");
    errorMode = Boolean.parseBoolean(System.getProperty(FrameworkProperty.ERROR_MODE));
    debugMode = Boolean.parseBoolean(System.getProperty(FrameworkProperty.DEBUG_MODE));
    gagPrintStream = new PrintStream(OutputStream.nullOutputStream());
    defaultStdout = System.out;
    defaultStderr = System.err;
    timeoutSec = 15;
  }

  private int successful = 0;
  private int failed = 0;
  private int aborted = 0;
  private int disabled = 0;
  private int repeatedTestInvocations = 0;
  private int successfulRepeatedTestInvocations = 0;
  private int failedRepeatedTestInvocations = 0;
  private int abortedRepeatedTestInvocations = 0;
  private int lastPrintedLineLength = 0;
  private long totalTime = 0;
  private long testTime = 0;
  private long repeatedTestTime = 0;
  private long beginning;
  private Timer timer;
  private String containerName;
  private String testName;
  private String repeatedTestSummary;
  private boolean repeatedTest;
  private boolean noClassDef;
  private boolean errorMode;
  private final PrintStream defaultStdout;
  private final PrintStream defaultStderr;
  private final PrintStream gagPrintStream;
  private final long timeoutSec;
  private final boolean debugMode;

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
    final var result = new StringJoiner(System.lineSeparator());
    result.add(String.format("Zeus can not find '%s'", error.getMessage()));
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
      System.setErr(defaultStdout);
    } else {
      System.setOut(gagPrintStream);
      System.setErr(gagPrintStream);
    }
    containerName = context.getDisplayName();
    if (!errorMode) {
      defaultStdout.print(prepareHeader());
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
      repeatedTestTime = 0;
      repeatedTestInvocations = 0;
      successfulRepeatedTestInvocations = 0;
      failedRepeatedTestInvocations = 0;
      abortedRepeatedTestInvocations = 0;
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
    timer.schedule(createTimer(), timeoutSec * 1_000);
    printEntry(State.RUNNING);
    beginning = System.nanoTime();
  }

  /**
   * Provide additional behavior to tests after each test is invoked.
   *
   * @param context the current extension context
   */
  @Override
  public void afterEach(ExtensionContext context) {
    testTime = System.nanoTime() - beginning;
    totalTime += testTime;
    if (repeatedTest) {
      repeatedTestTime += testTime;
    }
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
    if (repeatedTest) {
      successfulRepeatedTestInvocations++;
    }
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
      abortedRepeatedTestInvocations++;
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
    testName = prepareTestName(context);
    printEntry(State.DISABLED);
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
    clearLastLine();
    final var errors = failed + aborted + disabled;
    if (!errorMode || errors > 0) {
      defaultStdout.println(underline(prepareFooter()));
    } else if (errors == 0) {
      defaultStdout.printf(
          "%s is done successfully in %s!%n",
          containerName,
          prepareDuration(totalTime));
    }
    AnsiConsole.systemUninstall();
    System.setOut(defaultStdout);
    System.setErr(defaultStderr);
    gagPrintStream.close();
  }

  private void printEntry(final State state) {
    if (!noClassDef) {
      final var result = new StringBuilder();
      if (errorMode) {
        var errors = failed + aborted + disabled;
        if (errors == 1 && (state != State.SUCCESSFUL && state != State.RUNNING)) {
          result.append(prepareHeader());
        }
        if (errors == 0) {
          result.append(String.format("%s -> %s", containerName, testName));
        } else {
          result.append(String.format("%s", testName));
        }
      } else {
        result.append(String.format("%s", testName));
      }
      if (repeatedTest) {
        repeatedTestSummary = prepareSummary(state);
        result.append(String.format(" test %d", repeatedTestInvocations));
      }
      if (debugMode && state != State.RUNNING) {
        result.append(prepareTime(testTime));
      }
      result.append(prepareStatus(state));
      if (debugMode
          || (
          state != State.RUNNING
              && (state != State.SUCCESSFUL || (!errorMode && !repeatedTest))
              && !(state == State.NO_METHOD && repeatedTest))) {
        result.append(System.lineSeparator());
      }
      if (result.length() > lastPrintedLineLength) {
        lastPrintedLineLength = result.length();
      }
      clearLastLine();
      defaultStdout.print(result);
    }
  }

  private void printEntry(final State state, final Throwable cause) {
    if (!noClassDef) {
      if (state == State.NO_CLASS) {
        clearLastLine();
      } else {
        printEntry(state);
      }
      if (repeatedTest && state == State.NO_METHOD) {
        repeatedTestSummary += String.format("%n%s", prepareReason(state, cause));
      } else {
        defaultStdout.println(prepareReason(state, cause));
      }
    }
  }

  private void printSummary() {
    if (!noClassDef && !debugMode) {
      if (repeatedTest && successfulRepeatedTestInvocations > 0 && !errorMode) {
        clearLastLine();
        defaultStdout.println(repeatedTestSummary);
      }
    }
  }

  private void clearLastLine() {
    defaultStdout.print("\b".repeat(lastPrintedLineLength));
  }

  private String prepareHeader() {
    return String.format("Result of %s:%n%n", containerName);
  }

  private String prepareTestName(final ExtensionContext context) {
    final var name = context.getDisplayName();
    final var match = name.matches("^.+\\(.*$");
    return String.format("%s()", match ? name.substring(0, name.indexOf('(')) : name);
  }

  private String prepareFooter() {
    final var result = new StringJoiner(" | ");
    result.add(String.format("%nTOTAL %d", successful + failed + aborted + disabled));
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
    return result.add(String.format("TIME %s", prepareDuration(totalTime))).toString();
  }

  private String prepareSummary(final State state) {
    final var result = new StringBuilder().append(testName);
    if (repeatedTestInvocations == successfulRepeatedTestInvocations
        || repeatedTestInvocations == abortedRepeatedTestInvocations
        || repeatedTestInvocations == failedRepeatedTestInvocations) {
      if (repeatedTestInvocations > 1) {
        result.append(String.format(" %d tests", repeatedTestInvocations));
      }
      if (debugMode) {
        result.append(prepareTime(repeatedTestTime));
      }
      result.append(prepareStatus(state));
    } else {
      var suffix = successfulRepeatedTestInvocations > 1 ? "s" : "";
      result.append(
          String.format(
              " %d test%s of %d",
              successfulRepeatedTestInvocations,
              suffix,
              repeatedTestInvocations));
      if (debugMode) {
        result.append(prepareTime(repeatedTestTime));
      }
      result.append(prepareStatus(State.SUCCESSFUL));
    }
    return result.toString();
  }

  private String prepareStatus(final State state) {
    return Ansi.ansi().a(" - ").fg(state.color).a(state.status).reset().toString();
  }

  private String prepareTime(final long ns) {
    return String.format(" [%s]", prepareDuration(ns));
  }

  private String prepareDuration(final long ns) {
    if (ns < 1_000_000) {
      return String.format("%.2f ms", ns / 1_000_000f);
    }
    final var ms = ns / 1_000_000f;
    final var sec = (long) ms / 1_000;
    if (sec > 0) {
      var result = new StringJoiner(" ").add(String.format("%d sec", sec));
      var leftMs = ms - (1_000 * sec);
      if (leftMs > 0) {
        result.add(String.format("%.2f ms", leftMs));
      }
      return result.toString();
    }
    return String.format("%.2f ms", ms);
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
        failed++;
        testTime = TimeUnit.SECONDS.toNanos(timeoutSec);
        printEntry(State.INTERRUPTED, new TimeoutException());
        System.exit(0);
      }
    };
  }
}
