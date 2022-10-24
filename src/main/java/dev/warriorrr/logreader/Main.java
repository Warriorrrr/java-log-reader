package dev.warriorrr.logreader;

import dev.warriorrr.logreader.console.LogReaderConsole;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.util.PathConverter;
import joptsimple.util.PathProperties;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        OptionParser parser = new OptionParser();

        parser.acceptsAll(Arrays.asList("folder", "logsfolder"), "Path to the logs folder")
                .withRequiredArg()
                .withValuesConvertedBy(new PathConverter(PathProperties.DIRECTORY_EXISTING))
                .defaultsTo(Path.of("logs"));

        parser.accepts("fetchlogs", "Fetches & decompresses .gz compressed log files.")
                .withOptionalArg()
                .describedAs("The directory to fetch compressed logs from. Defaults to logs folder.")
                .withValuesConvertedBy(new PathConverter(PathProperties.DIRECTORY_EXISTING));

        OptionSet options;
        try {
            options = parser.parse(args);
        } catch (OptionException e) {
            e.printStackTrace();
            return;
        }

        final LogReader reader = new LogReader((Path) options.valueOf("logsfolder"));

        clearScreen();
        System.out.println("|##########################################################|");
        System.out.println("|                     LogReader v0.0.4                     |");
        System.out.println("|##########################################################|");
        System.out.println();
        System.out.println("Type 'help' for help.");
        System.out.println("Logs folder location: " + reader.logsPath().toAbsolutePath());

        // Fetch logs if option is present.
        // The fetchlogs option has an optional argument, if it's not present we'll use the logs folder
        if (options.has("fetchlogs"))
            reader.fetchCompressedLogs((Path) options.valueOfOptional("fetchlogs").orElse(options.valueOf("logsfolder")), (Path) options.valueOf("logsfolder"));

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
}