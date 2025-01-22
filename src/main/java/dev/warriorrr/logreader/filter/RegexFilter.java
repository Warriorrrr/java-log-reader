package dev.warriorrr.logreader.filter;

import java.util.regex.Pattern;

public class RegexFilter extends Filter {
    private final Pattern pattern;

    public RegexFilter(Pattern pattern) {
        super("lines matching the pattern '" + pattern.pattern() + "'");
        this.pattern = pattern;
    }

    @Override
    public boolean matches(String line, String lineLowercase) {
        return pattern.matcher(line).find();
    }
}
