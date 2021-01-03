package academy.kovalevskyi.testing.view;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

public class TestsConsolePrinter implements TestWatcher, BeforeAllCallback, AfterAllCallback {

  private int successful = 0;
  private int failed = 0;
  private boolean isNoClass;
  private static boolean isSilentMode;

  public static void setSilentMode(boolean silentMode) {
    isSilentMode = silentMode;
  }

  @Override
  public void beforeAll(ExtensionContext extensionContext) {
    AnsiConsole.systemInstall();
    if (!isSilentMode) {
      System.out.printf("Result of %s\n\n", extensionContext.getDisplayName());
    }
  }

  @Override
  public void testSuccessful(ExtensionContext context) {
    successful++;
    if (!isSilentMode) {
      System.out.println(Ansi
          .ansi()
          .a(context.getDisplayName())
          .a(" - ")
          .fgGreen()
          .a("OK")
          .reset());
    }
  }

  @Override
  public void testFailed(ExtensionContext context, Throwable cause) {
    failed++;
    if (!isNoClass) {
      if (cause instanceof NoClassDefFoundError) {
        isNoClass = true;
        System.out.println(Ansi
            .ansi()
            .fgRed()
            .a("Class under test not found, very-very likely you have incorrectly set class path\n")
            .a("or you have changed structure of the project so Zeus can not find the class below\n")
            .a("'")
            .a(cause.getMessage())
            .a("'")
            .reset());
      } else {
        System.out.println(Ansi
            .ansi()
            .a(context.getDisplayName())
            .a(" - ")
            .fgRed()
            .a("BAD")
            .a('\n')
            .a(cause.getMessage())
            .reset());
      }
    }
  }

  @Override
  public void afterAll(ExtensionContext extensionContext) {
    if (!isSilentMode) {
      System.out.printf("\nTotal: %d\n", successful + failed);
      System.out.printf("Successful: %d\n", successful);
      System.out.printf("Failed : %d\n", failed);
      System.out.print("------------------------------\n");
    }
    AnsiConsole.systemUninstall();
  }
}
