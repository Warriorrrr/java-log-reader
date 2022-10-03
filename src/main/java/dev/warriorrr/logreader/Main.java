package dev.warriorrr.logreader;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.util.PathConverter;
import joptsimple.util.PathProperties;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        OptionParser parser = new OptionParser();

        parser.acceptsAll(Arrays.asList("folder", "logsfolder"), "Path to the logs folder")
                .withRequiredArg()
                .withValuesConvertedBy(new PathConverter(PathProperties.DIRECTORY_EXISTING))
                .defaultsTo(Path.of("logs"));

        OptionSet options;
        try {
            options = parser.parse(args);
        } catch (OptionException e) {
            e.printStackTrace();
            return;
        }

        final LogReader reader = new LogReader((Path) options.valueOf("folder"));

        clearScreen();
        System.out.println("|##########################################################|");
        System.out.println("|                     LogReader v1.0.0                     |");
        System.out.println("|##########################################################|");
        System.out.println();
        System.out.println("Type 'help' for help.");
        System.out.println("Logs folder location: " + reader.logsPath().toAbsolutePath());

        final Scanner scanner = new Scanner(System.in);
        try {
            while (true) {
                String line = scanner.nextLine();
                if (line.isEmpty())
                    continue;

                if ("quit".equalsIgnoreCase(line) || "exit".equalsIgnoreCase(line))
                    break;

                reader.receiveCommand(line);
            }
        } catch (Exception ignored) {}
    }

    private static void clearScreen() {
        try {
            if (System.getProperty("os.name").startsWith("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                new ProcessBuilder("clear").inheritIO().start().waitFor();
        } catch (Exception ignored) {}
    }
}