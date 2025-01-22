package dev.warriorrr.logreader.file;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class LogFileAdapter implements FileAdapter {

    @Override
    public @Nullable BufferedReader adapt(@NotNull Path file) throws IOException {
        return Files.newBufferedReader(file, StandardCharsets.UTF_8);
    }

    @Override
    public Collection<String> supportedFileExtensions() {
        return List.of("log", "txt");
    }
}
