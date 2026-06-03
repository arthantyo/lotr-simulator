package nl.rug.oop.rts.controller.command;

import lombok.AllArgsConstructor;
import nl.rug.oop.rts.model.Edge;
import nl.rug.oop.rts.model.Graph;

/**
 * Command that removes an edge from the graph and restores it on undo.
 */
@AllArgsConstructor
public class DeleteEdgeCommand implements Command {
    /**
     * The graph the edge is removed from.
     */
    private final Graph graph;
    /**
     * The edge to remove.
     */
    private final Edge edge;

    @Override
    public void execute() {
        graph.deleteEdge(edge.getId());
    }

    @Override
    public void undo() {
        graph.addEdge(edge);
    }
}
