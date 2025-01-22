package dev.warriorrr.logreader.file;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class GZipFileAdapter implements FileAdapter {

    @Override
    public @Nullable BufferedReader adapt(@NotNull Path file) throws IOException {
        return new BufferedReader(new InputStreamReader(new GZIPInputStream(Files.newInputStream(file))));
    }

    @Override
    public Collection<String> supportedFileExtensions() {
        return List.of("gz");
    }
}
