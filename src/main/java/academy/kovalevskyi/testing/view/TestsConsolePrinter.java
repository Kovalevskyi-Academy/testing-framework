package academy.kovalevskyi.testing.view;

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
  private boolean noClassDef;
  private static boolean silentMode;

  public static void setSilentMode(boolean silent) {
    silentMode = silent;
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    AnsiConsole.systemInstall();
    if (!silentMode) {
      System.out.println(getHeader(context));
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
      System.out.println(getHeader(context));
    }
    failed++;
    if (!noClassDef) {
      System.out.println(getEntry(context, cause, "BAD", Color.RED));
    }
  }

  @Override
  public void afterAll(ExtensionContext context) {
    if (!silentMode) {
      System.out.printf("TOTAL %d | SUCCESSFUL %d | FAILED %d%n%n",
          successful + failed,
          successful,
          failed);
    } else if (failed == 0) {
      System.out.printf("%s is done successfully!%n", context.getDisplayName());
    } else {
      System.out.printf("You have %d failed method(s)%n%n", failed);
    }
    AnsiConsole.systemUninstall();
  }

  private String getHeader(ExtensionContext context) {
    return String.format("Result of %s:", context.getDisplayName());
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
          .a("- class is absent")
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
      message.a(cause.getMessage());
    }
    result.add(message.reset().toString());
    return result.toString();
  }

}
