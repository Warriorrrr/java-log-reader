package dev.warriorrr.logreader;

import dev.warriorrr.logreader.commands.ChangeDirCommand;
import dev.warriorrr.logreader.commands.Command;
import dev.warriorrr.logreader.commands.DateCommand;
import dev.warriorrr.logreader.commands.FilterCommand;
import dev.warriorrr.logreader.commands.HelpCommand;
import dev.warriorrr.logreader.commands.PrintCommand;
import dev.warriorrr.logreader.commands.SaveCommand;
import dev.warriorrr.logreader.commands.UndoCommand;
import dev.warriorrr.logreader.file.FileAdapter;
import dev.warriorrr.logreader.file.FileAdapters;
import dev.warriorrr.logreader.filter.Filter;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LogReader {
    private final LogReaderOptions options;
    private Path logsPath;
    private final Map<String, Command> commands = new HashMap<>();

    // Instead of storing all lines, store the active filters.
    private final List<Filter> appliedFilters = new ArrayList<>();

    private LocalDate minDate = null;
    private LocalDate maxDate = null;
    private static final Pattern FILE_NAME_DATE_PATTERN = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}");

    public LogReader(LogReaderOptions options) {
        this.options = options;
        this.logsPath = options.logsFolder;

        commands.put("help", new HelpCommand(this));
        commands.put("filter", new FilterCommand(this));
        commands.put("undo", new UndoCommand(this));
        commands.put("save", new SaveCommand(this));
        commands.put("print", new PrintCommand(this));
        commands.put("changedir", new ChangeDirCommand(this));
        commands.put("cd", commands.get("changedir"));
        commands.put("date", new DateCommand(this));
    }

    public void receiveCommand(String line) {
        String[] args = line.split(" ");
        if (args.length == 0)
            return;

        Command command = commands.get(args[0].toLowerCase(Locale.ROOT));
        if (command == null) {
            System.out.println("Unknown command: " + args[0]);
            return;
        }

        try {
            command.dispatch(Arrays.copyOfRange(args, 1, args.length));
        } catch (Exception e) {
            System.out.printf("Command '%s' threw exception when parsing command '%s'%n\n", args[0], Arrays.toString(args));
            e.printStackTrace();
        }
    }

    public long read(Consumer<String> lineConsumer) {
        if (!Files.exists(logsPath)) {
            System.out.printf("Path %s does not exist.", logsPath.toString());
            return 0;
        }

        final AtomicLong printed = new AtomicLong();

        final List<Path> candidateFiles = new ArrayList<>();


        try (Stream<Path> files = Files.list(logsPath)) {
            files.filter(Files::isRegularFile)
                .filter(this::checkFileDate)
                .sorted(Comparator.comparing(path -> path.getFileName().toString(), String::compareTo))
                .forEach(candidateFiles::add);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (final Path logFile : candidateFiles) {
            final FileAdapter adapter = FileAdapters.adapterFor(logFile);
            if (adapter == null) {
                System.out.println("Could not find a compatible adapter for file " + logFile);
                continue;
            }

            boolean fileNamePrinted = false;

            try (final BufferedReader reader = adapter.adapt(logFile)) {
                String line;
                lineLoop:
                while ((line = reader.readLine()) != null) {
                    final String lowercaseLine = line.toLowerCase(Locale.ROOT);

                    // All filters must pass for the line to be valid
                    for (final Filter filter : this.appliedFilters) {
                        if (!filter.matches(line, lowercaseLine))
                            continue lineLoop;
                    }

                    if (!fileNamePrinted) {
                        lineConsumer.accept("-- File: " + logFile + " --");
                        fileNamePrinted = true;
                    }

                    lineConsumer.accept(line);
                    printed.incrementAndGet();
                }
            } catch (IOException e) {
                System.out.println("An IO exception occurred while reading file " + logFile);
                e.printStackTrace();
            }
        }

        if (printed.get() == 0L)
            System.out.println("Done. No lines matched your filter(s)");
        else {
            System.out.printf("Done. Printed %d lines.", printed.get());
            System.out.println();
        }

        return printed.get();
    }

    /**
     * Checks whether the file is valid according to the current min and max date.
     * @param file The file to check
     * @return Whether the date for this file is between the min and max date
     */
    private boolean checkFileDate(final Path file) {
        if (minDate == null && maxDate == null) {
            return true;
        }

        final String fileName = file.getFileName().toString();
        LocalDate date;

        if (fileName.equals("latest.log")) {
            try {
                date = LocalDate.ofInstant(Files.getLastModifiedTime(file).toInstant(), ZoneId.systemDefault());
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            // parse date from file name
            final Matcher matcher = FILE_NAME_DATE_PATTERN.matcher(fileName);
            if (!matcher.find()) {
                return false;
            }

            date = LocalDate.parse(matcher.group());
        }

        if (minDate != null && date.isBefore(minDate)) {
            return false;
        }

        return maxDate == null || date.isBefore(maxDate);
    }

    public Path logsPath() {
        return logsPath;
    }

    public void logsPath(@NotNull Path path) {
        this.logsPath = path;
    }

    public void applyFilter(Filter filter) {
        this.appliedFilters.add(filter);
    }

    public List<Filter> appliedFilters() {
        return this.appliedFilters;
    }

    public void printFilters() {
        System.out.println("Currently applied filters:");

        final List<String> out = new ArrayList<>();
        for (Filter filter : this.appliedFilters) {
            out.add("- " + filter.description());
        }

        if (this.minDate != null) {
            out.add("- Logs after " + this.minDate);
        }

        if (this.maxDate != null) {
            out.add("- Logs before " + this.maxDate);
        }

        if (out.isEmpty())
            out.add("- none");

        for (final String line : out) {
            System.out.println(line);
        }
    }

    public Map<String, Command> commands() {
        return this.commands;
    }

    public void setMaxDate(LocalDate maxDate) {
        this.maxDate = maxDate;
    }

    public void setMinDate(LocalDate minDate) {
        this.minDate = minDate;
    }
}
