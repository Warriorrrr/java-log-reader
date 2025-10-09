package dev.warriorrr.logreader.commands;

import dev.warriorrr.logreader.LogReader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.regex.Pattern;

public class SaveCommand extends Command {
    private static final Pattern IPV4_PATTERN = Pattern.compile("(?:[0-9]{1,3}\\.){3}[0-9]{1,3}");

    private final Path outputFolder = Path.of("output");

    public SaveCommand(LogReader reader) {
        super(reader);
    }

    @Override
    public void dispatch(String[] args) {
        String fileName = args.length > 0 ? String.join("-", args) : generateFileName();
        Path output = outputFolder.resolve(fileName + ".log");

        try {
            if (!Files.exists(outputFolder)) {
                Files.createDirectories(outputFolder);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Saving log to './output/" + fileName + ".log'...");

        try (BufferedWriter buf = Files.newBufferedWriter(output, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
            reader.read(line -> {
                if (reader.options().censorIps) {
                    line = IPV4_PATTERN.matcher(line).replaceAll("*.*.*.*");
                }

                try {
                    buf.write(line);
                    buf.newLine();
                } catch (IOException e) {
                    System.out.println("An exception occurred when writing line in output file");
                    e.printStackTrace();
                }
            });

            System.out.println("Log saved as './output/" + fileName + ".log'.");
        } catch (IOException e) {
            System.out.println("An exception occurred when opening writer for output file");
            e.printStackTrace();
        }
    }

    private String generateFileName() {
        for (int i = 1; i < 10000; i++) {
            String fileName = String.format("output-%d", i);

            if (!Files.exists(outputFolder.resolve(fileName + ".log")))
                return fileName;
        }

        return "output";
    }
}
