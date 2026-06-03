package nl.rug.oop.rts.controller.command;

import lombok.AllArgsConstructor;
import nl.rug.oop.rts.model.Army;
import nl.rug.oop.rts.model.Graph;
import nl.rug.oop.rts.model.Node;

/**
 * Command that removes an army from a node and restores it on undo.
 */
@AllArgsConstructor
public class RemoveArmyCommand implements Command {
    /**
     * The graph the army is removed through.
     */
    private final Graph graph;
    /**
     * The node that contains the army.
     */
    private final Node node;
    /**
     * The army to remove.
     */
    private final Army army;

    @Override
    public void execute() {
        graph.removeArmyFromNode(node, army);
    }

    @Override
    public void undo() {
        graph.addArmyToNode(node, army);
    }
}
