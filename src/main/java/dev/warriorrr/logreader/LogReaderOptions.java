package dev.warriorrr.logreader;

import picocli.CommandLine.Option;

import java.nio.file.Path;

public class LogReaderOptions {
    @Option(names = { "--logs", "--logsfolder" }, description = "Folder containing the input log files", required = true)
    public Path logsFolder = Path.of("logs");

    @Option(names = { "--censorIpAddresses", "--censorIps" }, description = "Whether to censor ip addresses when saving results to a file.")
    public boolean censorIps = false;

    @Option(names = { "--output", "--outputfolder" }, description = "The output directory for logs saved via the save command")
    public Path outputFolder = Path.of("output");
}
