package dev.warriorrr.logreader.file;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class FileAdapterTest {
    @Test
    void testFileExtensionReading() {
        assertEquals(LogFileAdapter.class, Objects.requireNonNull(FileAdapters.adapterFor(Path.of("log.txt"))).getClass());
        assertEquals(LogFileAdapter.class, Objects.requireNonNull(FileAdapters.adapterFor(Path.of("log.log"))).getClass());
        assertNull(FileAdapters.adapterFor(Path.of("log.xyz")));
    }
}
