package dev.warriorrr.logreader.commands;

import dev.warriorrr.logreader.LogReader;

public abstract class Command {
    public final LogReader reader;

    public Command(LogReader reader) {
        this.reader = reader;
    }

    public abstract void dispatch(String[] args);
}
