package nl.rug.oop.rts.controller.command;

import lombok.AllArgsConstructor;
import nl.rug.oop.rts.model.Graph;
import nl.rug.oop.rts.model.Node;

/**
 * Command that adds a node to the graph and removes it again on undo.
 */
@AllArgsConstructor
public class AddNodeCommand implements Command {
    /**
     * The graph the node is added to.
     */
    private final Graph graph;
    /**
     * The node to add.
     */
    private final Node node;

    @Override
    public void execute() {
        graph.addNode(node);
    }

    @Override
    public void undo() {
        graph.deleteNode(node.getId());
    }
}
