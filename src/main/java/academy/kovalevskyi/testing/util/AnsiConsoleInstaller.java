package academy.kovalevskyi.testing.util;

import org.fusesource.jansi.AnsiConsole;

public class AnsiConsoleInstaller {

    public static final AnsiConsoleInstaller INSTANCE = new AnsiConsoleInstaller();

    private boolean installable = true;

    private AnsiConsoleInstaller(){}

    public void systemInstall() {
        if (!installable) {
            return;
        }
        try {
            AnsiConsole.systemInstall();
        } catch (Throwable t) {
            installable = false;
            // it is ok to proceed
        }
    }

    public void systemUninstall() {
        if (!installable) {
            return;
        }
        AnsiConsole.systemUninstall();
    }
}
