package nl.rug.oop.rts.controller.command;

/**
 * A reversible user action that can be executed, undone and redone.
 */
public interface Command {
    /**
     * Performs (or re-performs) the action.
     */
    void execute();

    /**
     * Reverts the action, restoring the previous state.
     */
    void undo();
}
