package dev.warriorrr.logreader.commands;

import dev.warriorrr.logreader.LogReader;
import dev.warriorrr.logreader.filter.ContainsFilter;
import dev.warriorrr.logreader.filter.Filter;
import dev.warriorrr.logreader.filter.InverseFilter;
import dev.warriorrr.logreader.filter.RegexFilter;

import java.util.Arrays;
import java.util.List;
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
                Filter filter = new ContainsFilter(phrase);

                if (exclude)
                    filter = new InverseFilter(filter);

                reader.applyFilter(filter);

                System.out.println("Successfully added filter. Use 'print' to print results.");
                reader.printFilters();
            }

            case "expression", "regex" -> {
                if (args.length < 2 || args[1].isEmpty()) {
                    System.out.println("No expression provided.");
                    return;
                }

                final String regexString = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

                try {
                    Pattern regex = Pattern.compile(regexString);
                    Filter filter = new RegexFilter(regex);

                    if (exclude)
                        filter = new InverseFilter(filter);

                    reader.applyFilter(filter);
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

    @Override
    public List<String> completions(List<String> args) {
        if (args.size() == 1)
            return Arrays.asList("exclude", "phrase", "expression");
        else if (args.size() == 2 && "exclude".equalsIgnoreCase(args.get(0)))
            return Arrays.asList("phrase", "expression");

        return super.completions(args);
    }
}
