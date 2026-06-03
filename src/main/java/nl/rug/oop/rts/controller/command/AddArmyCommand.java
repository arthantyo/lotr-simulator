package nl.rug.oop.rts.controller.command;

import lombok.AllArgsConstructor;
import nl.rug.oop.rts.model.Army;
import nl.rug.oop.rts.model.Graph;
import nl.rug.oop.rts.model.Node;

/**
 * Command that adds an army to a node and removes it again on undo.
 */
@AllArgsConstructor
public class AddArmyCommand implements Command {
    /**
     * The graph the army is added through.
     */
    private final Graph graph;
    /**
     * The node that receives the army.
     */
    private final Node node;
    /**
     * The army to add.
     */
    private final Army army;

    @Override
    public void execute() {
        graph.addArmyToNode(node, army);
    }

    @Override
    public void undo() {
        graph.removeArmyFromNode(node, army);
    }
}
