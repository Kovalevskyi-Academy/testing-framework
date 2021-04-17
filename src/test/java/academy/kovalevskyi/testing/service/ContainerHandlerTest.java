package academy.kovalevskyi.testing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.kovalevskyi.testing.test.five.TestClassEight;
import academy.kovalevskyi.testing.test.five.TestClassEleven;
import academy.kovalevskyi.testing.test.five.TestClassNine;
import academy.kovalevskyi.testing.test.five.TestClassSeven;
import academy.kovalevskyi.testing.test.five.TestClassTen;
import academy.kovalevskyi.testing.test.five.TestClassThirteen;
import academy.kovalevskyi.testing.test.five.TestClassTwelve;
import academy.kovalevskyi.testing.test.four.TestClassSix;
import academy.kovalevskyi.testing.test.one.TestClassOne;
import academy.kovalevskyi.testing.test.one.TestClassTwo;
import academy.kovalevskyi.testing.test.three.TestClassFive;
import academy.kovalevskyi.testing.test.two.TestClassThree;
import academy.kovalevskyi.testing.util.ContainerLauncher;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.fusesource.jansi.Ansi;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

public class ContainerHandlerTest {

  private static final PrintStream DEFAULT_STD_OUT = System.out;
  private static final String TIME_GAG = "#####";
  private static final String TEMPLATE = "((\\d+\\ssec\\s)*(\\d+|\\d+.\\d+)\\s(ms|sec))";
  private static final String MESSAGE = "Some message ==> expected: <true> but was: <false>";
  private static final ByteArrayOutputStream BUFFER = new ByteArrayOutputStream();

  @BeforeAll
  public static void beforeAll() {
    System.setOut(new PrintStream(BUFFER));
  }

  @AfterAll
  public static void afterAll() {
    System.out.close();
    System.setOut(DEFAULT_STD_OUT);
  }

  @AfterEach
  public void tearDown() {
    BUFFER.reset();
  }

  @Test
  public void testGetExceptionFromThrowableChain() {
    var expected = new IllegalArgumentException(new NullPointerException());
    var chain = new Exception(new RuntimeException(new UnsupportedOperationException(expected)));
    var actualFromChain =
        ContainerHandler.getExceptionFromThrowableChain(chain, IllegalArgumentException.class);
    var actualFromExpected =
        ContainerHandler.getExceptionFromThrowableChain(expected, IllegalArgumentException.class);

    assertTrue(actualFromExpected.isPresent());
    assertTrue(actualFromChain.isPresent());
    assertEquals(expected, actualFromChain.get());
  }

  @Test
  public void testDisabledTruthCleaning() {
    ContainerLauncher.execute(TestClassThree.class, false, false, false);
    assertTrue(
        Boolean.parseBoolean(
            System.getProperty("com.google.common.truth.disable_stack_trace_cleaning")));
  }

  @Test
  public void testDefaultStdOutIsNotBroken() {
    var expected = System.out;
    ContainerLauncher.execute(TestClassOne.class, false, false, false);
    var actual = System.out;
    assertEquals(expected, actual);
  }

  @Test
  public void testNoClassError() {
    ContainerLauncher.execute(TestClassEleven.class, false, false, false);
    var message = Ansi
        .ansi()
        .fg(State.NO_CLASS.color)
        .a("Zeus can not find 'SomeClass'\n"
            + "Reasons:\n"
            + "- your jar file is absent in the classpath\n"
            + "- class is absent in your jar file\n"
            + "- structure of the project is not default")
        .reset()
        .toString();
    var expected = String.format("Result of TestClassEleven:\n\n"
            + "%s\n\n"
            + "TOTAL 1 | FAILED 1 | TIME %s",
        message,
        TIME_GAG);

    checkEntries(expected);
  }

  @Test
  public void testNoMethods() {
    ContainerLauncher.execute(TestClassTwelve.class, false, false, false);
    var expected = String.format("Result of TestClassTwelve:\n\n"
            + "noMethodThree() 2 tests - %1$s\n"
            + "noMethodFour() 2 tests - %1$s\n"
            + "noMethodOne() - %1$s\n"
            + "noMethodTwo() 2 tests - %1$s\n\n"
            + "TOTAL 7 | FAILED 7 | TIME %2$s",
        prepareStatus(State.NO_METHOD, "someMethod\n"
            + "Reasons:\n"
            + "- method is absent\n"
            + "- signature of method is different"),
        TIME_GAG);

    checkEntries(expected);
  }

  @Test
  public void testNoMethodsWithDebugMode() {
    ContainerLauncher.execute(TestClassTwelve.class, false, true, false);
    var expected = String.format("Result of TestClassTwelve:\n\n"
            + "noMethodThree() test 1 - %2$s:\n"
            + "%3$s\n"
            + "noMethodThree() test 1 [%4$s] - %1$s\n"
            + "noMethodThree() test 2 - %2$s:\n"
            + "%3$s\n"
            + "noMethodThree() test 2 [%4$s] - %1$s\n"
            + "noMethodFour() 2 tests [%4$s] - %1$s\n"
            + "noMethodOne() [%4$s] - %1$s\n"
            + "noMethodTwo() test 2 - %2$s:\n"
            + "%3$s\n"
            + "noMethodTwo() test 2 [%4$s] - %1$s\n"
            + "noMethodTwo() 2 tests [%4$s] - %1$s\n\n"
            + "TOTAL 7 | FAILED 7 | DEBUG MODE ON | TIME %4$s",
        prepareStatus(State.NO_METHOD, "someMethod\n"
            + "Reasons:\n"
            + "- method is absent\n"
            + "- signature of method is different"),
        prepareStatus(State.RUNNING, null),
        "some text",
        TIME_GAG);

    checkEntries(expected);
  }

  @Test
  public void testNoMethodsWithDebugAndErrorMode() {
    ContainerLauncher.execute(TestClassTwelve.class, true, true, false);
    var expected = String.format("Result of TestClassTwelve:\n\n"
            + "noMethodThree() test 1 - %2$s:\n"
            + "%3$s\n"
            + "noMethodThree() test 1 [%4$s] - %1$s\n"
            + "noMethodThree() test 2 - %2$s:\n"
            + "%3$s\n"
            + "noMethodThree() test 2 [%4$s] - %1$s\n"
            + "noMethodFour() 2 tests [%4$s] - %1$s\n"
            + "noMethodOne() [%4$s] - %1$s\n"
            + "noMethodTwo() test 2 - %2$s:\n"
            + "%3$s\n"
            + "noMethodTwo() test 2 [%4$s] - %1$s\n"
            + "noMethodTwo() 2 tests [%4$s] - %1$s\n\n"
            + "TOTAL 7 | FAILED 7 | ERROR MODE ON | DEBUG MODE ON | TIME %4$s",
        prepareStatus(State.NO_METHOD, "someMethod\n"
            + "Reasons:\n"
            + "- method is absent\n"
            + "- signature of method is different"),
        prepareStatus(State.RUNNING, null),
        "some text",
        TIME_GAG);

    checkEntries(expected);
  }

  @Test
  public void testUnexpectedExceptions() {
    ContainerLauncher.execute(TestClassTen.class, false, false, false);
    var expected = String.format("Result of TestClassTen:\n\n"
            + "withNoMessage() - %s\n"
            + "successful() - %s\n"
            + "withMessage() - %s\n\n"
            + "TOTAL 3 | SUCCESSFUL 1 | FAILED 2 | TIME %s",
        prepareStatus(State.FAILED, "Thrown unexpected java.lang.Exception: "
            + "The error message is not provided\n"
            + "at academy.kovalevskyi.testing.test.five."
            + "TestClassTen.withNoMessage(TestClassTen.java:14)"),
        prepareStatus(State.SUCCESSFUL, null),
        prepareStatus(State.FAILED, "Thrown unexpected java.lang.Exception: Some message\n"
            + "at academy.kovalevskyi.testing.test.five."
            + "TestClassTen.withMessage(TestClassTen.java:19)"),
        TIME_GAG);

    checkEntries(expected);
  }

  @Test
  public void testDisplayNameAnnotation() {
    ContainerLauncher.execute(TestClassThree.class, false, false, false);
    var expected = String.format("Result of SomeNameOfTestClass:\n\n"
            + "SomeNameOfTestMethod1() - %1$s\n"
            + "SomeNameOfTestMethod2() 2 tests - %1$s\n"
            + "SomeNameOfTestMethod3() - %1$s\n"
            + "SomeNameOfTestMethod4() 2 tests - %1$s\n\n"
            + "TOTAL 6 | SUCCESSFUL 6 | TIME %2$s",
        prepareStatus(State.SUCCESSFUL, null),
        TIME_GAG);

    checkEntries(expected);
  }

  @Test
  public void testAllAvailableStatusesAndCaptorOff() {
    ContainerLauncher.execute(TestClassSeven.class, false, false, false);
    var expected = String.format("Result of TestClassSeven:\n\n"
            + "withPrintsBad() - %1$s\n"
            + "withRepeatedPrintsOk() 2 tests - %2$s\n"
            + "withRepeatedPrintsBad() test 1 - %1$s\n"
            + "withRepeatedPrintsBad() test 2 - %1$s\n"
            + "withPrintsOk() - %2$s\n"
            + "withRepeatedPrintsAborted() test 1 - %3$s\n"
            + "withRepeatedPrintsAborted() test 2 - %3$s\n"
            + "withPrintsAborted() - %3$s\n"
            + "withPrintsDisabled() - %4$s\n\n"
            + "TOTAL 10 | SUCCESSFUL 3 | FAILED 3 | ABORTED 3 | DISABLED 1 | TIME %5$s",
        prepareStatus(State.FAILED, MESSAGE),
        prepareStatus(State.SUCCESSFUL, null),
        prepareStatus(State.ABORTED, "Assumption failed: assumption is not true"),
        prepareStatus(State.DISABLED, "The disabled reason is not provided"),
        TIME_GAG);

    checkEntries(expected);
  }

  @RepeatedTest(100)
  public void testAllAvailableStatusesAndCaptorOn() {
    ContainerLauncher.execute(TestClassSeven.class, false, true, false);
    var expected = String.format("Result of TestClassSeven:\n\n"
            + "withPrintsBad() - %8$s:\n"
            + "%2$s\n"
            + "withPrintsBad() [%9$s] - %1$s\n"
            + "withRepeatedPrintsOk() test 1 - %8$s:\n"
            + "%4$s\n"
            + "withRepeatedPrintsOk() test 1 [%9$s] - %3$s\n"
            + "withRepeatedPrintsOk() test 2 - %8$s:\n"
            + "%4$s\n"
            + "withRepeatedPrintsOk() test 2 [%9$s] - %3$s\n"
            + "withRepeatedPrintsOk() 2 tests [%9$s] - %3$s\n"
            + "withRepeatedPrintsBad() test 1 - %8$s:\n"
            + "%2$s\n"
            + "withRepeatedPrintsBad() test 1 [%9$s] - %1$s\n"
            + "withRepeatedPrintsBad() test 2 - %8$s:\n"
            + "%2$s\n"
            + "withRepeatedPrintsBad() test 2 [%9$s] - %1$s\n"
            + "withPrintsOk() - %8$s:\n"
            + "%4$s\n"
            + "withPrintsOk() [%9$s] - %3$s\n"
            + "withRepeatedPrintsAborted() test 1 - %8$s:\n"
            + "%6$s\n"
            + "withRepeatedPrintsAborted() test 1 [%9$s] - %5$s\n"
            + "withRepeatedPrintsAborted() test 2 - %8$s:\n"
            + "%6$s\n"
            + "withRepeatedPrintsAborted() test 2 [%9$s] - %5$s\n"
            + "withPrintsAborted() - %8$s:\n"
            + "%6$s\n"
            + "withPrintsAborted() [%9$s] - %5$s\n"
            + "withPrintsDisabled() [%9$s] - %7$s\n\n"
            + "TOTAL 10 | SUCCESSFUL 3 | FAILED 3 | ABORTED 3 "
            + "| DISABLED 1 | DEBUG MODE ON | TIME %9$s",
        prepareStatus(State.FAILED, MESSAGE),
        "Some bad text",
        prepareStatus(State.SUCCESSFUL, null),
        "Some ok text",
        prepareStatus(State.ABORTED, "Assumption failed: assumption is not true"),
        "Some aborted text",
        prepareStatus(State.DISABLED, "The disabled reason is not provided"),
        prepareStatus(State.RUNNING, null),
        TIME_GAG);

    checkEntries(expected);
  }

  @RepeatedTest(100)
  public void testAllAvailableStatusesAndCaptorOnWithErrorMode() {
    ContainerLauncher.execute(TestClassSeven.class, true, true, false);
    var expected = String.format("Result of TestClassSeven:\n\n"
            + "withPrintsBad() - %8$s:\n"
            + "%2$s\n"
            + "withPrintsBad() [%9$s] - %1$s\n"
            + "withRepeatedPrintsOk() test 1 - %8$s:\n"
            + "%4$s\n"
            + "withRepeatedPrintsOk() test 1 [%9$s] - %3$s\n"
            + "withRepeatedPrintsOk() test 2 - %8$s:\n"
            + "%4$s\n"
            + "withRepeatedPrintsOk() test 2 [%9$s] - %3$s\n"
            + "withRepeatedPrintsBad() test 1 - %8$s:\n"
            + "%2$s\n"
            + "withRepeatedPrintsBad() test 1 [%9$s] - %1$s\n"
            + "withRepeatedPrintsBad() test 2 - %8$s:\n"
            + "%2$s\n"
            + "withRepeatedPrintsBad() test 2 [%9$s] - %1$s\n"
            + "withPrintsOk() - %8$s:\n"
            + "%4$s\n"
            + "withPrintsOk() [%9$s] - %3$s\n"
            + "withRepeatedPrintsAborted() test 1 - %8$s:\n"
            + "%6$s\n"
            + "withRepeatedPrintsAborted() test 1 [%9$s] - %5$s\n"
            + "withRepeatedPrintsAborted() test 2 - %8$s:\n"
            + "%6$s\n"
            + "withRepeatedPrintsAborted() test 2 [%9$s] - %5$s\n"
            + "withPrintsAborted() - %8$s:\n"
            + "%6$s\n"
            + "withPrintsAborted() [%9$s] - %5$s\n"
            + "withPrintsDisabled() [%9$s] - %7$s\n\n"
            + "TOTAL 10 | SUCCESSFUL 3 | FAILED 3 | ABORTED 3 | DISABLED 1 "
            + "| ERROR MODE ON | DEBUG MODE ON | TIME %9$s",
        prepareStatus(State.FAILED, MESSAGE),
        "Some bad text",
        prepareStatus(State.SUCCESSFUL, null),
        "Some ok text",
        prepareStatus(State.ABORTED, "Assumption failed: assumption is not true"),
        "Some aborted text",
        prepareStatus(State.DISABLED, "The disabled reason is not provided"),
        prepareStatus(State.RUNNING, null),
        TIME_GAG);

    checkEntries(expected);
  }

  @Test
  public void testTimeStamp() {
    ContainerLauncher.execute(TestClassOne.class, false, true, false);
    var entries = prepareConsoleView().split("\n");
    DEFAULT_STD_OUT.println(prepareConsoleView());
    assertEquals(5, entries.length - 4);
    for (var index = 1; index < entries.length - 1; index++) {
      if (entries[index].isEmpty()) {
        continue;
      }
      DEFAULT_STD_OUT.println(entries[index]);
      assertTrue(entries[index].matches(".+((\\d+\\ssec\\s)*(\\d+|\\d+.\\d+)\\s(ms|sec)).*"));
    }
  }

  @Test
  public void testSummaryOfRepeatedTest() {
    ContainerLauncher.execute(TestClassFive.class, false, false, false);
    var expected = String.format("Result of TestClassFive:\n\n"
            + "repeatedThree() - %2$s\n"
            + "repeatedParameterizedOne() test 2 - %1$s\n"
            + "repeatedParameterizedOne() 1 test of 2 - %2$s\n"
            + "repeatedParameterizedTwo() test 2 - %1$s\n"
            + "repeatedParameterizedTwo() 2 tests of 3 - %2$s\n"
            + "repeatedOne() test 1 - %1$s\n"
            + "repeatedOne() test 2 - %1$s\n"
            + "repeatedTwo() test 1 - %1$s\n\n"
            + "TOTAL 9 | SUCCESSFUL 4 | FAILED 5 | TIME %3$s",
        prepareStatus(State.FAILED, MESSAGE),
        prepareStatus(State.SUCCESSFUL, null),
        TIME_GAG);

    checkEntries(expected);
  }

  @Test
  public void testErrorMessageCutterAndTrimmer() {
    ContainerLauncher.execute(TestClassSix.class, false, false, false);
    var expected = String.format("Result of TestClassSix:\n\n"
            + "simpleOne() - %1$s\n"
            + "simpleTwo() - %2$s\n\n"
            + "TOTAL 2 | FAILED 2 | TIME %3$s",
        prepareStatus(State.FAILED, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
            + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
            + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
            + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
            + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
            + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
            + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
            + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!...\n"
            + "The message is too long, see instruction on how to enable verbose output"),
        prepareStatus(State.FAILED, MESSAGE),
        TIME_GAG);

    checkEntries(expected);
  }

  @Test
  public void testAllTestsBadWithVerboseMode() {
    ContainerLauncher.execute(TestClassSix.class, false, false, true);
    var expected = String.format("Result of TestClassSix:\n\n"
            + "simpleOne() - %1$s\n"
            + "simpleTwo() - %2$s\n\n"
            + "TOTAL 2 | FAILED 2 | VERBOSE MODE ON | TIME %3$s",
        prepareStatus(State.FAILED, "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
            + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
            + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
            + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
            + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
            + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
            + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
            + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ==> expected: <true> but was: <false>"),
        prepareStatus(State.FAILED, MESSAGE),
        TIME_GAG);

    checkEntries(expected);
  }

  @Test
  public void testAllTestsAreOkWithNoEnabledParameters() {
    ContainerLauncher.execute(TestClassOne.class, false, false, false);
    var expected = String.format("Result of TestClassOne:\n\n"
            + "simple() - %1$s\n"
            + "repeated() 2 tests - %1$s\n"
            + "parameterized() - %1$s\n"
            + "repeatedParameterized() 2 tests - %1$s\n\n"
            + "TOTAL 6 | SUCCESSFUL 6 | TIME %2$s",
        prepareStatus(State.SUCCESSFUL, null),
        TIME_GAG);

    checkEntries(expected);
  }

  @Test
  public void testAllTestsAreOkWithDebugMode() {
    ContainerLauncher.execute(TestClassOne.class, false, true, false);
    var expected = String.format("Result of TestClassOne:\n\n"
            + "simple() [%2$s] - %1$s\n"
            + "repeated() 2 tests [%2$s] - %1$s\n"
            + "parameterized() [%2$s] - %1$s\n"
            + "repeatedParameterized() 2 tests [%2$s] - %1$s\n\n"
            + "TOTAL 6 | SUCCESSFUL 6 | DEBUG MODE ON | TIME %2$s",
        prepareStatus(State.SUCCESSFUL, null),
        TIME_GAG);

    checkEntries(expected);
  }

  @Test
  public void testAllTestsAreOkWithErrorMode() {
    ContainerLauncher.execute(TestClassOne.class, true, false, false);
    var expected = String.format("Result of TestClassOne:\n\n"
            + "You have no errors! Good job!\n\n"
            + "TOTAL 6 | SUCCESSFUL 6 | ERROR MODE ON | TIME %s",
        TIME_GAG);

    checkEntries(expected);
  }

  @Test
  public void testAllTestsAreOkWithErrorAndDebugModes() {
    ContainerLauncher.execute(TestClassThirteen.class, true, true, false);
    var expected = String.format("Result of TestClassThirteen:\n\n"
            + "simple() - %s:\n"
            + "Some ok text\n"
            + "simple() [%3$s] - %s\n\n"
            + "TOTAL 6 | SUCCESSFUL 6 | ERROR MODE ON | DEBUG MODE ON | TIME %3$s",
        prepareStatus(State.RUNNING, null),
        prepareStatus(State.SUCCESSFUL, null),
        TIME_GAG);

    checkEntries(expected);
  }

  @Test
  public void testAllTestsAreAbortedAndOneOkWithNoEnabledParameters() {
    ContainerLauncher.execute(TestClassEight.class, false, false, false);
    var expected = String.format("Result of TestClassEight:\n\n"
            + "simpleOne() - %1$s\n"
            + "simpleTwo() - %2$s\n"
            + "successful() - %3$s\n"
            + "repeated() test 1 - %2$s\n"
            + "repeated() test 2 - %2$s\n"
            + "parameterized() test 1 - %2$s\n"
            + "repeatedParameterized() test 1 - %2$s\n"
            + "repeatedParameterized() test 2 - %2$s\n\n"
            + "TOTAL 8 | SUCCESSFUL 1 | ABORTED 7 | TIME %4$s",
        prepareStatus(State.ABORTED, "Assumption failed: assumption is not true"),
        prepareStatus(State.ABORTED, "Assumption failed: Some message"),
        prepareStatus(State.SUCCESSFUL, null),
        TIME_GAG);

    checkEntries(expected);
  }

  @Test
  public void testAllTestsAreAbortedAndOneOkWithDebugMode() {
    ContainerLauncher.execute(TestClassEight.class, false, true, false);
    var expected = String.format("Result of TestClassEight:\n\n"
            + "simpleOne() [%4$s] - %1$s\n"
            + "simpleTwo() [%4$s] - %2$s\n"
            + "successful() [%4$s] - %3$s\n"
            + "repeated() test 1 [%4$s] - %2$s\n"
            + "repeated() test 2 [%4$s] - %2$s\n"
            + "parameterized() test 1 [%4$s] - %2$s\n"
            + "repeatedParameterized() test 1 [%4$s] - %2$s\n"
            + "repeatedParameterized() test 2 [%4$s] - %2$s\n\n"
            + "TOTAL 8 | SUCCESSFUL 1 | ABORTED 7 | DEBUG MODE ON | TIME %4$s",
        prepareStatus(State.ABORTED, "Assumption failed: assumption is not true"),
        prepareStatus(State.ABORTED, "Assumption failed: Some message"),
        prepareStatus(State.SUCCESSFUL, null),
        TIME_GAG);

    checkEntries(expected);
  }

  @Test
  public void testAllTestsAreAbortedAndOneOkWithDebugAndErrorMode() {
    ContainerLauncher.execute(TestClassEight.class, true, true, false);
    var expected = String.format("Result of TestClassEight:\n\n"
            + "simpleOne() [%4$s] - %1$s\n"
            + "simpleTwo() [%4$s] - %2$s\n"
            + "repeated() test 1 [%4$s] - %2$s\n"
            + "repeated() test 2 [%4$s] - %2$s\n"
            + "parameterized() test 1 [%4$s] - %2$s\n"
            + "repeatedParameterized() test 1 [%4$s] - %2$s\n"
            + "repeatedParameterized() test 2 [%4$s] - %2$s\n\n"
            + "TOTAL 8 | SUCCESSFUL 1 | ABORTED 7 | ERROR MODE ON | DEBUG MODE ON | TIME %4$s",
        prepareStatus(State.ABORTED, "Assumption failed: assumption is not true"),
        prepareStatus(State.ABORTED, "Assumption failed: Some message"),
        prepareStatus(State.SUCCESSFUL, null),
        TIME_GAG);

    checkEntries(expected);
  }

  @Test
  public void testAllTestsAreDisabledAndOneOkWithNoEnabledParameters() {
    ContainerLauncher.execute(TestClassNine.class, false, false, false);
    var expected = String.format("Result of TestClassNine:\n\n"
            + "successful() - %1$s\n"
            + "parameterized() - %2$s\n"
            + "repeatedParameterized() - %2$s\n"
            + "disabledOne() - %2$s\n"
            + "disabledTwo() - %3$s\n\n"
            + "TOTAL 5 | SUCCESSFUL 1 | DISABLED 4 | TIME %4$s",
        prepareStatus(State.SUCCESSFUL, null),
        prepareStatus(State.DISABLED, "The disabled reason is not provided"),
        prepareStatus(State.DISABLED, "Some reason"),
        TIME_GAG);

    checkEntries(expected);
  }

  @Test
  public void testAllTestsAreDisabledAndOneOkWithDebugMode() {
    ContainerLauncher.execute(TestClassNine.class, false, true, false);
    var expected = String.format("Result of TestClassNine:\n\n"
            + "successful() [%4$s] - %1$s\n"
            + "parameterized() [%4$s] - %2$s\n"
            + "repeatedParameterized() [%4$s] - %2$s\n"
            + "disabledOne() [%4$s] - %2$s\n"
            + "disabledTwo() [%4$s] - %3$s\n\n"
            + "TOTAL 5 | SUCCESSFUL 1 | DISABLED 4 | DEBUG MODE ON | TIME %4$s",
        prepareStatus(State.SUCCESSFUL, null),
        prepareStatus(State.DISABLED, "The disabled reason is not provided"),
        prepareStatus(State.DISABLED, "Some reason"),
        TIME_GAG);

    checkEntries(expected);
  }

  @Test
  public void testAllTestsAreDisabledAndOneOkWithDebugAndErrorMode() {
    ContainerLauncher.execute(TestClassNine.class, true, true, false);
    var expected = String.format("Result of TestClassNine:\n\n"
            + "parameterized() [%3$s] - %1$s\n"
            + "repeatedParameterized() [%3$s] - %1$s\n"
            + "disabledOne() [%3$s] - %1$s\n"
            + "disabledTwo() [%3$s] - %2$s\n\n"
            + "TOTAL 5 | SUCCESSFUL 1 | DISABLED 4 | ERROR MODE ON | DEBUG MODE ON | TIME %3$s",
        prepareStatus(State.DISABLED, "The disabled reason is not provided"),
        prepareStatus(State.DISABLED, "Some reason"),
        TIME_GAG);

    checkEntries(expected);
  }

  @Test
  public void testAllTestsAreBadAndOneOkWithNoEnabledParameters() {
    ContainerLauncher.execute(TestClassTwo.class, false, false, false);
    var expected = String.format("Result of TestClassTwo:\n\n"
            + "simple() - %1$s\n"
            + "successful() - %2$s\n"
            + "repeated() test 1 - %1$s\n"
            + "repeated() test 2 - %1$s\n"
            + "parameterized() test 1 - %1$s\n"
            + "repeatedParameterized() test 1 - %1$s\n"
            + "repeatedParameterized() test 2 - %1$s\n\n"
            + "TOTAL 7 | SUCCESSFUL 1 | FAILED 6 | TIME %3$s",
        prepareStatus(State.FAILED, MESSAGE),
        prepareStatus(State.SUCCESSFUL, null),
        TIME_GAG);

    checkEntries(expected);
  }

  @Test
  public void testAllTestsAreBadAndOneOkWithDebugMode() {
    ContainerLauncher.execute(TestClassTwo.class, false, true, false);
    var expected = String.format("Result of TestClassTwo:\n\n"
            + "simple() [%3$s] - %1$s\n"
            + "successful() [%3$s] - %2$s\n"
            + "repeated() test 1 [%3$s] - %1$s\n"
            + "repeated() test 2 [%3$s] - %1$s\n"
            + "parameterized() test 1 [%3$s] - %1$s\n"
            + "repeatedParameterized() test 1 [%3$s] - %1$s\n"
            + "repeatedParameterized() test 2 [%3$s] - %1$s\n\n"
            + "TOTAL 7 | SUCCESSFUL 1 | FAILED 6 | DEBUG MODE ON | TIME %3$s",
        prepareStatus(State.FAILED, MESSAGE),
        prepareStatus(State.SUCCESSFUL, null),
        TIME_GAG);

    checkEntries(expected);
  }

  @Test
  public void testAllTestsAreBadAndOneOkWithDebugAndErrorMode() {
    ContainerLauncher.execute(TestClassTwo.class, true, true, false);
    var expected = String.format("Result of TestClassTwo:\n\n"
            + "simple() [%2$s] - %1$s\n"
            + "repeated() test 1 [%2$s] - %1$s\n"
            + "repeated() test 2 [%2$s] - %1$s\n"
            + "parameterized() test 1 [%2$s] - %1$s\n"
            + "repeatedParameterized() test 1 [%2$s] - %1$s\n"
            + "repeatedParameterized() test 2 [%2$s] - %1$s\n\n"
            + "TOTAL 7 | SUCCESSFUL 1 | FAILED 6 | ERROR MODE ON | DEBUG MODE ON | TIME %2$s",
        prepareStatus(State.FAILED, MESSAGE),
        TIME_GAG);

    checkEntries(expected);
  }

  @Test
  public void testAllTestsAreBadAndOneOkWithErrorMode() {
    ContainerLauncher.execute(TestClassTwo.class, true, false, false);
    var expected = String.format("Result of TestClassTwo:\n\n"
            + "simple() - %1$s\n"
            + "repeated() test 1 - %1$s\n"
            + "repeated() test 2 - %1$s\n"
            + "parameterized() test 1 - %1$s\n"
            + "repeatedParameterized() test 1 - %1$s\n"
            + "repeatedParameterized() test 2 - %1$s\n\n"
            + "TOTAL 7 | SUCCESSFUL 1 | FAILED 6 | ERROR MODE ON | TIME %3$s",
        prepareStatus(State.FAILED, MESSAGE),
        prepareStatus(State.SUCCESSFUL, null),
        TIME_GAG);

    checkEntries(expected);
  }

  private void checkEntries(String expected) {
    var expectedLines = expected.split("\n");
    var actualLines = prepareConsoleView().split("\n");

    assertEquals(expectedLines.length + 1, actualLines.length);

    IntStream
        .range(0, expectedLines.length)
        .forEach(index -> {
          var preparedLine =
              actualLines[index].replaceAll(TEMPLATE, TIME_GAG);
          assertEquals(expectedLines[index], preparedLine);
        });

    assertEquals(
        actualLines[actualLines.length - 2].length(),
        actualLines[actualLines.length - 1].length());

    assertTrue(BUFFER.toString().endsWith("\n"));

    for (var symbol : actualLines[actualLines.length - 1].toCharArray()) {
      assertEquals('-', symbol);
    }
  }

  private String prepareStatus(State state, String message) {
    if (message != null) {
      return Ansi.ansi()
          .fg(state.color)
          .format("%s\u001b[m\n", state.status)
          .fg(state.color)
          .format("%s", message)
          .reset()
          .toString();
    }
    return Ansi.ansi().fg(state.color).a(state.status).reset().toString();
  }

  private String prepareConsoleView() {
    var result = BUFFER.toString().replaceAll("\r", "");
    return Arrays.stream(result.split("\n"))
        .map(entry -> {
          var lastIndex = entry.lastIndexOf("  ");
          if (lastIndex != -1) {
            return entry.substring(lastIndex + 2);
          }
          return entry;
        })
        .collect(Collectors.joining("\n"));
  }
}