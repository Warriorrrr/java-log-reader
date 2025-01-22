package dev.warriorrr.logreader.filter;

public class ContainsFilter extends Filter {
    private final String mustContain;

    public ContainsFilter(String mustContain) {
        super("lines containing '" + mustContain + "'");
        this.mustContain = mustContain;
    }

    @Override
    public boolean matches(String line, String lineLowercase) {
        return lineLowercase.contains(mustContain);
    }
}
