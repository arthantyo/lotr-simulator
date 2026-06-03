package nl.rug.oop.rts.controller.command;

import lombok.AllArgsConstructor;
import nl.rug.oop.rts.model.Node;

/**
 * Command that records a node being moved from one position to another,
 * so a whole drag gesture can be undone in a single step.
 */
@AllArgsConstructor
public class MoveNodeCommand implements Command {
    /**
     * The node that was moved.
     */
    private Node node;
    /**
     * X coordinate (world space) before the move.
     */
    private int oldX;
    /**
     * Y coordinate (world space) before the move.
     */
    private int oldY;
    /**
     * X coordinate (world space) after the move.
     */
    private int newX;
    /**
     * Y coordinate (world space) after the move.
     */
    private int newY;

    @Override
    public void execute() {
        node.setX(newX);
        node.setY(newY);
    }

    @Override
    public void undo() {
        node.setX(oldX);
        node.setY(oldY);
    }
}
