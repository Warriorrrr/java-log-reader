package dev.warriorrr.logreader;

import picocli.CommandLine.Option;

import java.nio.file.Path;

public class LogReaderOptions {
    @Option(names = { "--logs", "--logsfolder" }, description = "Folder containing the input log files", required = true)
    Path logsFolder = Path.of("logs");
}
