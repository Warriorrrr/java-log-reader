package dev.warriorrr.logreader.file;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FileAdapters {
    private static final Map<String, FileAdapter> ADAPTERS = new HashMap<>();

    public static @Nullable FileAdapter adapterFor(final @NotNull Path file) {
        final String fileName = file.getFileName().toString();
        final int lastDotIndex = fileName.lastIndexOf('.');

        if (lastDotIndex == -1)
            return null;

        final String extension = fileName.substring(lastDotIndex + 1);
        return ADAPTERS.get(extension);
    }

    public static void registerAdapter(FileAdapter adapter) {
        adapter.supportedFileExtensions().forEach(extension -> ADAPTERS.put(extension, adapter));
    }

    static {
        registerAdapter(new LogFileAdapter());
        registerAdapter(new GZipFileAdapter());
    }
}
