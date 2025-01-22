package dev.warriorrr.logreader.filter;

public class InverseFilter extends Filter {
    private final Filter filter;

    public InverseFilter(final Filter filter) {
        super("Inverse of " + filter.description());
        this.filter = filter;
    }

    @Override
    public boolean matches(String line, String lineLowercase) {
        return !this.filter.matches(line, lineLowercase);
    }
}
