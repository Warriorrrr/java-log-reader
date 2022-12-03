package dev.warriorrr.logreader.commands;

import dev.warriorrr.logreader.LogReader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class SaveCommand extends Command {

    private final Path outputFolder = Path.of("output");

    public SaveCommand(LogReader reader) {
        super(reader);
    }

    @Override
    public void dispatch(String[] args) {
        String fileName = args.length > 0 ? String.join("-", args) : generateFileName();
        Path output = outputFolder.resolve(fileName + ".log");

        try {
            if (!Files.exists(outputFolder))
                Files.createDirectories(outputFolder);

            Files.deleteIfExists(output);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Saving log to './output/" + fileName + ".log'...");

        try (BufferedWriter buf = Files.newBufferedWriter(output, StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
            reader.read(line -> {
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
