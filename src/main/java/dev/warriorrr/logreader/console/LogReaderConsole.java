package dev.warriorrr.logreader.console;

import dev.warriorrr.logreader.LogReader;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.nio.file.Path;

public class LogReaderConsole {
    private final LogReader reader;

    public LogReaderConsole(LogReader reader) {
        this.reader = reader;
    }

    public void readCommands() {
        Terminal terminal;
        try {
            terminal = TerminalBuilder.builder().system(true).build();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        LineReader lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .appName("java-log-reader")
                .completer(new CommandCompleter(reader))
                .variable(LineReader.HISTORY_FILE, Path.of(".command_history"))
                .build();

        try {
            while (true) {
                String line = lineReader.readLine("> ");
                if (line.isEmpty())
                    continue;

                if ("quit".equalsIgnoreCase(line) || "exit".equalsIgnoreCase(line) || "bye".equalsIgnoreCase(line))
                    break;

                reader.receiveCommand(line);
                System.out.println();
            }
        } catch (UserInterruptException | EndOfFileException ignored) {
            // Process was interrupted by user, likely ctrl+c, don't log any exceptions.
        } catch (Exception e) {
            System.out.println("An exception occurred when reading input");
            e.printStackTrace();
        }
    }
}
