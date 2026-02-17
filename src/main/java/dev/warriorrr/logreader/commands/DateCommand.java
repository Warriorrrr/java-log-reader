package dev.warriorrr.logreader.commands;

import dev.warriorrr.logreader.LogReader;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DateCommand extends Command {
    private final List<String> dateFormatStrings = List.of(
        "dd-MM-yyyy",
        "dd/MM/yyyy",
        "yyyy-MM-dd",
        "dd MM yyyy"
    );

    private final List<DateTimeFormatter> dateFormats = dateFormatStrings.stream().map(DateTimeFormatter::ofPattern).toList();

    public DateCommand(LogReader reader) {
        super(reader);
    }

    @Override
    public void dispatch(String[] args) {
        if (args.length < 1) {
            System.err.println("Not enough arguments! Usage: date <min|max> [<date>]");
            return;
        }

        final String arg = args[0].toLowerCase(Locale.ROOT);
        if (!arg.equals("min") && !arg.equals("max")) {
            System.err.println("Invalid argument, usage: date <min|max> [<date>]");
            return;
        }

        final boolean minDate = "min".equals(arg);

        if (args.length < 2) {
            // clear date
            if (minDate) {
                reader.setMinDate(null);
            } else {
                reader.setMaxDate(null);
            }

            System.out.println("Cleared " + (minDate ? "minimum" : "maximum") + " date.");
            return;
        }

        final List<String> argsList = new ArrayList<>(List.of(args));
        argsList.removeFirst();

        final LocalDate date = parseDate(String.join(" ", argsList));
        if (date == null) {
            System.err.println("Invalid date format specified, format must be one of [" + String.join(", ", this.dateFormatStrings) + "]");
            return;
        }

        if (minDate) {
            reader.setMinDate(date);
            System.out.println("Minimum date set to " + date);
        } else {
            reader.setMaxDate(date);
            System.out.println("Maximum date set to " + date);
        }
    }

    private LocalDate parseDate(final String string) {
        for (final DateTimeFormatter format : this.dateFormats) {
            try {
                return LocalDate.parse(string, format);
            } catch (DateTimeParseException ignored) {}
        }

        return null;
    }

    @Override
    public List<String> completions(List<String> args) {
        if (args.size() == 1) {
            return List.of("min", "max");
        }

        return List.of();
    }
}
