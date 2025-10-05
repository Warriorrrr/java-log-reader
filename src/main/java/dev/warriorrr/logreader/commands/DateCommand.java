package dev.warriorrr.logreader.commands;

import dev.warriorrr.logreader.LogReader;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class DateCommand extends Command {
    public DateCommand(LogReader reader) {
        super(reader);
    }

    @Override
    public void dispatch(String[] args) {
        if (args.length < 1) {
            System.out.println("Not enough arguments! Usage: date <min|max> [<date>]");
            return;
        }

        final String arg = args[0].toLowerCase(Locale.ROOT);
        if (!arg.equals("min") && !arg.equals("max")) {
            System.out.println("Invalid argument, usage: date <min|max> <date>");
            return;
        }

        final LocalDate date = args.length > 1 ? LocalDate.parse(args[1]) : null;
        if (arg.equals("min")) {
            reader.setMinDate(date);
            System.out.println("Minimum date set to " + date);
        } else {
            reader.setMaxDate(date);
            System.out.println("Maximum date set to " + date);
        }
    }

    @Override
    public List<String> completions(List<String> args) {
        if (args.size() == 1) {
            return List.of("min", "max");
        }

        return List.of();
    }
}
