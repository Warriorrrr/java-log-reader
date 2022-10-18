package dev.warriorrr.logreader.console;

import dev.warriorrr.logreader.LogReader;
import dev.warriorrr.logreader.commands.Command;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;

public class CommandCompleter implements Completer {
    private final LogReader reader;

    public CommandCompleter(LogReader reader) {
        this.reader = reader;
    }

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        if (line.wordIndex() == 0) {
            for (String command : this.reader.commands().keySet()) {
                if (command.regionMatches(true, 0, line.word(), 0, line.word().length()))
                    candidates.add(new Candidate(command));
            }
        } else {
            Command command = this.reader.commands().get(line.words().get(0));
            if (command == null)
                return;

            for (String completion : command.completions(line.words().subList(1, line.words().size())))
                candidates.add(new Candidate(completion));
        }
    }
}
