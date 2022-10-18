package dev.warriorrr.logreader.commands;

import dev.warriorrr.logreader.LogReader;

import java.util.Collections;
import java.util.List;

public abstract class Command {
    public final LogReader reader;

    public Command(LogReader reader) {
        this.reader = reader;
    }

    public abstract void dispatch(String[] args);

    public List<String> completions(List<String> args) {
        return Collections.emptyList();
    }
}
