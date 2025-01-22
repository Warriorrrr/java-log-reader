package dev.warriorrr.logreader.file;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

public interface FileAdapter {
    BufferedReader adapt(@NotNull Path file) throws IOException;

    Collection<String> supportedFileExtensions();
}
