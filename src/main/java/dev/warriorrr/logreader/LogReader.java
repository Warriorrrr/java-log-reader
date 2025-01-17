package dev.warriorrr.logreader;

import dev.warriorrr.logreader.commands.ChangeDirCommand;
import dev.warriorrr.logreader.commands.Command;
import dev.warriorrr.logreader.commands.FilterCommand;
import dev.warriorrr.logreader.commands.HelpCommand;
import dev.warriorrr.logreader.commands.PrintCommand;
import dev.warriorrr.logreader.commands.SaveCommand;
import dev.warriorrr.logreader.commands.UndoCommand;
import dev.warriorrr.logreader.filter.Filter;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public class LogReader {
    private Path logsPath;
    private final Map<String, Command> commands = new HashMap<>();

    // Instead of storing all lines, store the active filters.
    private final List<Filter> appliedFilters = new ArrayList<>();

    public LogReader(Path logsPath) {
        this.logsPath = logsPath;

        commands.put("help", new HelpCommand(this));
        commands.put("filter", new FilterCommand(this));
        commands.put("undo", new UndoCommand(this));
        commands.put("save", new SaveCommand(this));
        commands.put("print", new PrintCommand(this));
        commands.put("changedir", new ChangeDirCommand(this));
        commands.put("cd", commands.get("changedir"));
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

        final Predicate<String> allMatches = appliedFilters.stream()
                .filter(Filter::requireAllMatch)
                .map(filter -> (Predicate<String>) filter::matches)
                .reduce(x -> true, Predicate::and);

        final Predicate<String> anyMatches = appliedFilters.stream()
                .filter(filter -> !filter.requireAllMatch())
                .map(filter -> (Predicate<String>) filter::matches)
                .reduce(x -> false, Predicate::or);

        final AtomicLong printed = new AtomicLong();

        try (final Stream<Path> files = Files.list(logsPath)) {
            files.filter(file -> file.getFileName().toString().endsWith(".log"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString(), String::compareTo))
                    .forEach(logFile -> {
                        final AtomicBoolean fileNamePrinted = new AtomicBoolean(false);

                        try (final Stream<String> lines = Files.lines(logFile)) {
                            lines.forEach(line -> {
                                if (allMatches.test(line) || anyMatches.test(line)) {
                                    if (!fileNamePrinted.getAndSet(true))
                                        lineConsumer.accept("-- File: " + logFile + " --");

                                    lineConsumer.accept(line);
                                    printed.incrementAndGet();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (printed.get() == 0L)
            System.out.println("Done. No lines matched your filter(s)");
        else {
            System.out.printf("Done. Printed %d lines.", printed.get());
            System.out.println();
        }

        return printed.get();
    }

    public Path logsPath() {
        return logsPath;
    }

    public void logsPath(@NotNull Path path) {
        this.logsPath = path;
    }

    public void applyFilter(String description, Predicate<String> predicate, boolean allMatch) {
        // TODO: make proper filter classes
        this.appliedFilters.add(new Filter(description) {
            @Override
            public boolean matches(String line) {
                return predicate.test(line);
            }

            @Override
            public boolean requireAllMatch() {
                return allMatch;
            }
        });
    }

    public List<Filter> appliedFilters() {
        return this.appliedFilters;
    }

    public void printFilters() {
        System.out.println("Currently applied filters:");

        for (Filter filter : this.appliedFilters) {
            System.out.println("- " + filter.description());
        }

        if (this.appliedFilters.isEmpty())
            System.out.println("- none");
    }

    public Map<String, Command> commands() {
        return this.commands;
    }

    public void fetchCompressedLogs(Path fetchDir, Path targetDir) {
        System.out.println();
        System.out.println("Looking for compressed logs in " + targetDir.toAbsolutePath() + "...");

        try (Stream<Path> files = Files.list(fetchDir)) {
            files.filter(file -> file.getFileName().toString().endsWith(".gz"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString(), String::compareTo))
                    .forEach(file -> {
                        Path target = targetDir.resolve(file.getFileName().toString().substring(0, file.getFileName().toString().lastIndexOf(".gz")));
                        if (Files.exists(target))
                            return;

                        System.out.println("Decompressing " + file + "...");

                        try (GZIPInputStream gzipIn = new GZIPInputStream(Files.newInputStream(file));
                            BufferedWriter out = Files.newBufferedWriter(target);
                            BufferedReader in = new BufferedReader(new InputStreamReader(gzipIn, StandardCharsets.UTF_8))) {

                            String string;
                            while ((string = in.readLine()) != null) {
                                out.write(string);
                                out.newLine();
                            }
                        } catch (IOException e) {
                            System.out.println("An exception occurred when decompressing log file " + file + ".");
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            System.out.println("An exception occurred when decompressing logs");
            e.printStackTrace();
        }

        if (Files.exists(fetchDir.resolve("latest.log"))) {
            System.out.println("Grabbing latest.log...");

            try {
                Files.copy(fetchDir.resolve("latest.log"), targetDir.resolve("latest.log"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println("An exception occurred when copying latest.log");
                e.printStackTrace();
            }
        }

        System.out.println("Finished fetching logs.");
    }
}
