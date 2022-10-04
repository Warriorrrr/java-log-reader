package dev.warriorrr.logreader.commands;

import dev.warriorrr.logreader.LogReader;

public class HelpCommand extends Command {
    public HelpCommand(LogReader reader) {
        super(reader);
    }

    @Override
    public void dispatch(String[] args) {
        System.out.println("Log Reader Commands");
        System.out.println("- filter phrase <phrase>            | Include all lines containing the specified phrase.");
        System.out.println("- filter exclude phrase <phrase>    | Exclude all lines containing the specified phrase.");
        System.out.println("- filter expression <regex>         | Include all lines matching the specified regex.");
        System.out.println("- filter exclude expression <regex> | Exclude all lines matching the specified regex.");
        System.out.println("- help                              | Show this text.");
        System.out.println("- print                             | Prints all lines matching the filters added using the filter command.");
        System.out.println("- save [filename]                   | Saves all lines matching the filters added using the filter command to an output file.");
        System.out.println("- undo                              | Removes the most recently added filter.");
    }
}
