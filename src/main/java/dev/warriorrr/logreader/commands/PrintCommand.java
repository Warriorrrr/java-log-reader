package dev.warriorrr.logreader.commands;

import dev.warriorrr.logreader.LogReader;

public class PrintCommand extends Command {
    public PrintCommand(LogReader reader) {
        super(reader);
    }

    @Override
    public void dispatch(String[] args) {
        reader.read(System.out::println);
    }
}
