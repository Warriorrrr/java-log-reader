package dev.warriorrr.logreader.filter;

import org.jetbrains.annotations.NotNull;

public abstract class Filter {
    private final String description;

    public Filter(final @NotNull String description) {
        this.description = description;
    }

    public String description() {
        return this.description;
    }

    public abstract boolean matches(final String line, final String lineLowercase);
}
