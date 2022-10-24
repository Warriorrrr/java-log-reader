package dev.warriorrr.logreader.commands;

import dev.warriorrr.logreader.LogReader;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public class ChangeDirCommand extends Command {
    public ChangeDirCommand(LogReader reader) {
        super(reader);
    }

    @Override
    public void dispatch(String[] args) {
        if (args.length == 0) {
            System.out.println("Not enough arguments, usage: cd <folder>");
            return;
        }

        Path path;
        try {
             path = Path.of(args[0]);
        } catch (InvalidPathException e) {
            System.out.println(args[0] + " is not a valid path.");
            return;
        }

        if (!Files.exists(path) || !Files.isDirectory(path)) {
            System.out.println(path.toAbsolutePath() + " does not exist or is not a directory.");
            return;
        }

        reader.logsPath(path);
        System.out.println("Changed logs path to " + path.toAbsolutePath());
    }
}
