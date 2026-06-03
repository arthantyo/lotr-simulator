package nl.rug.oop.rts.controller;

import java.util.Deque;
import java.util.ArrayDeque;
import nl.rug.oop.rts.controller.command.Command;

/**
 * Keeps track of executed commands so user actions can be undone and redone.
 * Maintains an undo stack and a redo stack following the Command pattern.
 */
public class CommandManager {
    /**
     * Commands that have been executed and can be undone (most recent on top).
     */
    private final Deque<Command> undoStack = new ArrayDeque<>();

    /**
     * Commands that have been undone and can be redone (most recent on top).
     */
    private final Deque<Command> redoStack = new ArrayDeque<>();

    /**
     * Callback invoked whenever the stacks change, used to refresh the UI.
     */
    private Runnable onChange = () -> { };

    /**
     * Sets the callback invoked whenever the undo/redo stacks change.
     *
     * @param onChange the callback to run on every change
     */
    public void setOnChange(Runnable onChange) {
        this.onChange = onChange;
    }

    /**
     * Indicates whether there is a command that can be undone.
     *
     * @return true if the undo stack is not empty
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * Indicates whether there is a command that can be redone.
     *
     * @return true if the redo stack is not empty
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /**
     * Executes a brand-new command and records it. Clears the redo history.
     *
     * @param command the command to execute
     */
    public void executeCommand(Command command) {
        command.execute();
        undoStack.push(command);
        redoStack.clear();
        onChange.run();
    }

    /**
     * Undoes the most recently executed command, if any.
     */
    public void undo() {
        if (undoStack.isEmpty()) {
            return;
        }
        Command command = undoStack.pop();
        command.undo();
        redoStack.push(command);
        onChange.run();
    }

    /**
     * Redoes the most recently undone command, if any.
     */
    public void redo() {
        if (redoStack.isEmpty()) {
            return;
        }
        Command command = redoStack.pop();
        command.execute();
        undoStack.push(command);
        onChange.run();
    }
}
