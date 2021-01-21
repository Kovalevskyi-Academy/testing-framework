package academy.kovalevskyi.testing.view;

import java.util.Objects;
import java.util.StringJoiner;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.Ansi.Color;
import org.fusesource.jansi.AnsiConsole;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

public class TestsConsolePrinter implements TestWatcher, BeforeAllCallback, AfterAllCallback {

  private int successful = 0;
  private int failed = 0;
  private String className;
  private boolean noClassDef;
  private static boolean silentMode;

  public static void setSilentMode(boolean silent) {
    silentMode = silent;
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    AnsiConsole.systemInstall();
    className = context.getDisplayName();
    if (!silentMode) {
      System.out.println(getHeader());
    }
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
    if (!silentMode) {
      var result = String.format("%nTOTAL %d | SUCCESSFUL %d | FAILED %d%n",
          successful + failed,
          successful,
          failed);
      System.out.printf("%s%s%n", result, "-".repeat(result.length() - 4));
    } else if (failed == 0) {
      System.out.printf("%s is done successfully!%n", context.getDisplayName());
    } else {
      var result = String.format("%nYou have %d failed method(s)%n", failed);
      System.out.printf("%s%s%n", result, "-".repeat(result.length() - 4));
    }
    AnsiConsole.systemUninstall();
  }

  private String getHeader() {
    return String.format("Result of %s:%n", className);
  }

  private String getEntry(ExtensionContext context, String status, Color color) {
    return Ansi
        .ansi()
        .format("%s - ", context.getDisplayName())
        .fg(color)
        .a(status)
        .reset()
        .toString();
  }

  private String getEntry(ExtensionContext context, Throwable cause, String status, Color color) {
    if (cause instanceof NoClassDefFoundError) {
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
    if (cause instanceof NoSuchMethodError) {
      message.format("'%s' is absent, write the method", cause.getMessage());
    } else {
      message.a(
          Objects.requireNonNullElse(
              cause.getMessage(),
              String.format("by %s", cause.getClass().getName())));
    }
    result.add(message.reset().toString());
    return result.toString();
  }

}