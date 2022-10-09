package dev.warriorrr.logreader.commands;

import dev.warriorrr.logreader.LogReader;

public class PrintCommand extends Command {
    public PrintCommand(LogReader reader) {
        super(reader);
    }

    @Override
    public void dispatch(String[] args) {
        if (args.length > 0 && "--matches".equalsIgnoreCase(args[0])) {
            System.out.println(reader.read(s -> {}) + " lines match the current filters.");
            return;
        }

        reader.read(System.out::println);
    }
}
