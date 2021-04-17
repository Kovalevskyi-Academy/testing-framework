package academy.kovalevskyi.testing.service;

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

  private final long timeoutSec = 15;
  private final boolean errorMode;
  private final boolean debugMode;
  private final boolean verboseMode;
  private final Timer timer = new Timer(true);
  private final PrintStream stdOut = System.out;
  private final PrintStream stdErr = System.err;
  private final PrintStreamWrapper wrapper = new PrintStreamWrapper(System.out);
  private int successful = 0;
  private int failed = 0;
  private int aborted = 0;
  private int disabled = 0;
  private int repeatedTestInvocations = 0;
  private int repeatedTestInvocationsWithNoSuchMethodAndPrints = 0;
  private int successfulRepeatedTestInvocations = 0;
  private int failedRepeatedTestInvocations = 0;
  private int abortedRepeatedTestInvocations = 0;
  private int lastPrintedLineLength = 0;
  private long totalTime = 0;
  private long testTime = 0;
  private long repeatedTestTime = 0;
  private long beginning = 0;
  private String testName;
  private String repeatedTestSummary;
  private TimerTask timerTask;
  private boolean repeatedTest = false;
  private boolean noClassDef = false;
  private boolean noSuchMethod = false;
  private boolean printedDuringMethod = false;
  private boolean printedDuringErrorMode = false;

  {
    System.setProperty("com.google.common.truth.disable_stack_trace_cleaning", "true");
    errorMode = Boolean.parseBoolean(System.getProperty(FrameworkProperty.ERROR_MODE));
    debugMode = Boolean.parseBoolean(System.getProperty(FrameworkProperty.DEBUG_MODE));
    verboseMode = Boolean.parseBoolean(System.getProperty(FrameworkProperty.VERBOSE_MODE));
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
    System.setOut(wrapper);
    System.setErr(wrapper);
    stdOut.printf("Result of %s:%n%n", context.getDisplayName());

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
      noSuchMethod = false;
    } else if (entryUniqueId.matches("^.+\\[test-template:.+\\)]$")) {
      testName = prepareTestName(context);
      printSummary();
      repeatedTest = true;
      repeatedTestTime = 0;
      repeatedTestInvocations = 0;
      successfulRepeatedTestInvocations = 0;
      failedRepeatedTestInvocations = 0;
      abortedRepeatedTestInvocations = 0;
      repeatedTestInvocationsWithNoSuchMethodAndPrints = 0;
    } else if (entryUniqueId.matches("^.+\\[test-template-invocation:#\\d+]$")) {
      repeatedTestInvocations++;
      noSuchMethod = false;
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
    timerTask = createNewTask();
    timer.schedule(timerTask, timeoutSec * 1_000);
    printEntry(State.RUNNING);
    if (debugMode) {
      wrapper.enable();
    }
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
    if (debugMode) {
      printedDuringMethod = wrapper.hasContent();
      if (errorMode && printedDuringMethod) {
        printedDuringErrorMode = true;
      }
      wrapper.disable();
    }
    timerTask.cancel();
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
    printEntry(State.DISABLED, new Exception(reason.orElse(null)));
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
      noSuchMethod = true;
      if (repeatedTest && printedDuringMethod) {
        repeatedTestInvocationsWithNoSuchMethodAndPrints++;
      }
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
    if (errorMode && errors == 0 && !printedDuringErrorMode) {
      stdOut.println("You have no errors! Good job!");
    }
    stdOut.println(underline(prepareFooter()));
    AnsiConsole.systemUninstall();
    wrapper.destroy();
    timer.cancel();
    System.setOut(stdOut);
    System.setErr(stdErr);
  }

  private void printEntry(final State state) {
    if (!noClassDef) {
      final var result = new StringBuilder();
      result.append(String.format("%s", testName));
      if (repeatedTest) {
        repeatedTestSummary = prepareSummary(state);
        result.append(String.format(" test %d", repeatedTestInvocations));
      }
      if (debugMode && state != State.RUNNING) {
        result.append(prepareTime(testTime));
      }
      result.append(prepareStatus(state));
      if (state != State.RUNNING
          && (
          state != State.SUCCESSFUL
              || (errorMode && printedDuringMethod)
              || (!errorMode && !repeatedTest)
              || (debugMode && repeatedTest && printedDuringMethod))
          && (!(state == State.NO_METHOD && repeatedTest) || printedDuringMethod)) {
        result.append(System.lineSeparator());
      }
      if (result.length() > lastPrintedLineLength) {
        lastPrintedLineLength = result.length();
      }
      clearLastLine();
      stdOut.print(result);
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
        if (printedDuringMethod) {
          stdOut.println(prepareReason(state, cause));
        }
      } else {
        stdOut.println(prepareReason(state, cause));
      }
    }
  }

  private void printSummary() {
    if (!noClassDef && repeatedTest
        && (
        (successfulRepeatedTestInvocations > 0 && !errorMode)
            || (
            noSuchMethod
                && repeatedTestInvocationsWithNoSuchMethodAndPrints != repeatedTestInvocations))) {
      clearLastLine();
      stdOut.println(repeatedTestSummary);
    }
  }

  private void clearLastLine() {
    stdOut.printf("\r%s\r", " ".repeat(lastPrintedLineLength));
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
    if (errorMode) {
      result.add("ERROR MODE ON");
    }
    if (debugMode) {
      result.add("DEBUG MODE ON");
    }
    if (verboseMode) {
      result.add("VERBOSE MODE ON");
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

  private String prepareDisabledMessage(final Throwable cause) {
    var message = cause.getMessage();
    if (message != null && message.matches("^.+\\) is @Disabled$")) {
      return "The disabled reason is not provided";
    }
    return prepareCauseMessage(cause);
  }

  private String prepareCauseMessage(final Throwable cause) {
    var message = cause.getMessage();
    if (message != null && !message.isBlank()) {
      message = message.trim();
      final var messageMaxLength = 600;
      if (!verboseMode && message.length() > messageMaxLength) {
        return String.format(
            "%s...%n%s",
            message.substring(0, messageMaxLength),
            "The message is too long, see instruction on how to enable verbose output");
      } else {
        return message;
      }
    } else {
      return "The error message is not provided";
    }
  }

  private String prepareReason(final State state, final Throwable cause) {
    final var result = Ansi.ansi().fg(state.color);
    if (state == State.NO_CLASS) {
      result.a(getReason((NoClassDefFoundError) cause));
    } else if (state == State.NO_METHOD) {
      result.format("%s%n", cause.getMessage().trim());
      result.format("Reasons:%n");
      result.format("- method is absent%n");
      result.a("- signature of method is different");
    } else if (state == State.INTERRUPTED) {
      result.format("Time (%d sec) is out! Something went wrong...%n", timeoutSec);
    } else if (state == State.DISABLED) {
      result.a(prepareDisabledMessage(cause));
    } else if (cause instanceof AssertionError || cause instanceof TestAbortedException) {
      result.a(prepareCauseMessage(cause));
    } else {
      result.format(
          "Thrown unexpected %s: %s%n",
          cause.getClass().getName(),
          prepareCauseMessage(cause));
      result.format("at %s", cause.getStackTrace()[0].toString());
    }
    return result.reset().toString();
  }

  private TimerTask createNewTask() {
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
