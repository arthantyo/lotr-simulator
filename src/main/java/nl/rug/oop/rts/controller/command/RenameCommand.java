package nl.rug.oop.rts.controller.command;

import nl.rug.oop.rts.model.Nameable;

/**
 * Command that renames a {@link Nameable} graph element (a node or an edge)
 * and restores the previous name on undo.
 */
public class RenameCommand implements Command {
    /**
     * The node or edge being renamed.
     */
    private Nameable target;
    /**
     * The name to apply on execute.
     */
    private String newName;
    /**
     * The name captured before the change, restored on undo.
     */
    private String oldName;

    /**
     * Creates a command that renames the given target. The current name is
     * captured at construction time so it can be restored on undo.
     *
     * @param target  the node or edge to rename
     * @param newName the new name to apply
     */
    public RenameCommand(Nameable target, String newName) {
        this.target = target;
        this.newName = newName;
        this.oldName = target.getName();
    }

    @Override
    public void execute() {
        target.setName(newName);
    }

    @Override
    public void undo() {
        target.setName(oldName);
    }
}
