package dev.warriorrr.logreader;

import dev.warriorrr.logreader.console.LogReaderConsole;
import picocli.CommandLine;

import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        final LogReaderOptions options = new LogReaderOptions();

        new CommandLine(options).parseArgs(args);

        final LogReader reader = new LogReader(options);

        clearScreen();
        System.out.println("|##########################################################|");
        System.out.println("|                     LogReader v%version%                     |".replace("%version%", getVersion()));
        System.out.println("|##########################################################|");
        System.out.println();
        System.out.println("Type 'help' for help.");
        System.out.println("Logs folder location: " + reader.logsPath().toAbsolutePath());

        new LogReaderConsole(reader).readCommands();
    }

    private static void clearScreen() {
        try {
            if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                new ProcessBuilder("clear").inheritIO().start().waitFor();
        } catch (Exception ignored) {}
    }

    private static String getVersion() {
        return Main.class.getPackage().getSpecificationVersion();
    }
}