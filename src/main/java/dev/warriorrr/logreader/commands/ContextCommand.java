package dev.warriorrr.logreader.commands;

import dev.warriorrr.logreader.LogReader;

public class ContextCommand extends Command {
    public ContextCommand(LogReader reader) {
        super(reader);
    }

    @Override
    public void dispatch(String[] args) {
        if (args.length < 1) {
            System.out.println("Not enough arguments, usage: context <length>");
            return;
        }

        final int context;
        try {
            context = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println(args[0] + " is not a valid number.");
            return;
        }

        reader.setContextLength(context);
        System.out.println("Context length set to " + context + " lines before/after matches.");
    }
}
