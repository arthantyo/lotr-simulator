package nl.rug.oop.rts.controller.command;

import java.util.List;
import java.util.ArrayList;
import nl.rug.oop.rts.model.Edge;
import nl.rug.oop.rts.model.Graph;
import nl.rug.oop.rts.model.Node;

/**
 * Command that removes a node from the graph. Because removing a node also
 * removes every connected edge, those edges are captured so they can be
 * restored on undo.
 */
public class DeleteNodeCommand implements Command {
    /**
     * The graph the node is removed from.
     */
    private final Graph graph;
    /**
     * The node to remove.
     */
    private final Node node;
    /**
     * Edges removed alongside the node, captured so they can be restored.
     */
    private List<Edge> deletedEdges = new ArrayList<>();

    /**
     * Creates a command that removes the given node from the given graph.
     *
     * @param graph the graph to remove the node from
     * @param node  the node to remove
     */
    public DeleteNodeCommand(Graph graph, Node node) {
        this.graph = graph;
        this.node = node;
    }

    @Override
    public void execute() {
        deletedEdges = new ArrayList<>();
        for (Edge edge : graph.getEdges()) {
            if (edge.getFrom() == node || edge.getTo() == node) {
                deletedEdges.add(edge);
            }
        }
        graph.deleteNode(node.getId());
    }

    @Override
    public void undo() {
        graph.addNode(node);
        for (Edge edge : deletedEdges) {
            graph.addEdge(edge);
        }
    }
}
