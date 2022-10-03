package dev.warriorrr.logreader.commands;

import dev.warriorrr.logreader.LogReader;

public class UndoCommand extends Command {

    public UndoCommand(LogReader reader) {
        super(reader);
    }

    @Override
    public void dispatch(String[] args) {
        if (!reader.appliedFilters().isEmpty()) {
            reader.appliedFilters().remove(reader.appliedFilters().size() - 1);
            System.out.println("Last action undone.");
            reader.printFilters();
        } else
            System.out.println("No actions left to undo.");
    }
}
