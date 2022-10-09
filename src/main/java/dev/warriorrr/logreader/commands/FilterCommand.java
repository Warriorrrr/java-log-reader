package dev.warriorrr.logreader.commands;

import dev.warriorrr.logreader.LogReader;

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class FilterCommand extends Command {

    public FilterCommand(LogReader reader) {
        super(reader);
    }

    @Override
    public void dispatch(String[] args) {
        boolean exclude = false;

        if (args.length > 0 && "exclude".equalsIgnoreCase(args[0])) {
            exclude = true;
            args = Arrays.copyOfRange(args, 1, args.length);
        }

        switch (args.length > 0 ? args[0].toLowerCase(Locale.ROOT) : "") {
            case "phrase" -> {
                if (args.length < 2 || args[1].isEmpty()) {
                    System.out.println("No search term provided.");
                    return;
                }

                final String phrase = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).toLowerCase(Locale.ROOT);

                if (exclude)
                    reader.applyFilter("lines NOT containing '" + phrase + "'", line -> !line.toLowerCase(Locale.ROOT).contains(phrase), true);
                else
                    reader.applyFilter("lines containing '" + phrase + "'", line -> line.toLowerCase(Locale.ROOT).contains(phrase), true);

                System.out.println("Successfully added filter. Use 'print' to print results.");
                reader.printFilters();
            }

            case "expression", "regex" -> {
                if (args.length < 2 || args[1].isEmpty()) {
                    System.out.println("No expression provided.");
                    return;
                }

                final String regexString = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).toLowerCase(Locale.ROOT);

                try {
                    Pattern regex = Pattern.compile(regexString);

                    if (exclude)
                        reader.applyFilter("patterns NOT matching the regex '" + regexString + "'", line -> !regex.matcher(line).find(), true);
                    else
                        reader.applyFilter("lines matching the regex '" + regexString + "'", line -> regex.matcher(line).find(), true);
                } catch (PatternSyntaxException e) {
                    System.out.println("Invalid regex format: " + regexString);
                    System.out.println(e.getMessage());
                    return;
                }

                System.out.println("Successfully added filter. Use 'print' to print results.");
                reader.printFilters();
            }

            default -> reader.printFilters();
        }
    }
}
