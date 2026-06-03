package nl.rug.oop.rts.controller.command;

import lombok.AllArgsConstructor;
import nl.rug.oop.rts.model.Edge;
import nl.rug.oop.rts.model.Graph;

/**
 * Command that adds an edge to the graph and removes it again on undo.
 */
@AllArgsConstructor
public class AddEdgeCommand implements Command {
    /**
     * The graph the edge is added to.
     */
    private final Graph graph;
    /**
     * The edge to add.
     */
    private final Edge edge;

    @Override
    public void execute() {
        graph.addEdge(edge);
    }

    @Override
    public void undo() {
        graph.deleteEdge(edge.getId());
    }
}
