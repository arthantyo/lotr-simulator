package nl.rug.oop.rts.view;

import nl.rug.oop.rts.model.Edge;
import nl.rug.oop.rts.model.Node;

/**
 * Listener base class for receiving updates about changes and interactions
 * with the graph. Subclasses may override any of the callback methods.
 */
public class GraphListener {
    /**
     * Called when a node is added to the graph.
     * @param node Node that has been added to the graph.
     */
    public void onNodeAdded(Node node) {
        // do nothing by default
    }

    /**
     * Called when a node is removed from the graph.
     * @param node Node that has been removed from the graph.
     */
    public void onNodeDeleted(Node node) {
        // do nothing by default
    }

    /**
     * Called when an edge is added to the graph.
     * @param edge Edge that has been added to the graph.
     */
    public void onEdgeAdded(Edge edge) {
        // do nothing by default
    }

    /**
     * Called when an edge is removed from the graph.
     * @param edge Edge that has been removed from the graph.
     */
    public void onEdgeDeleted(Edge edge) {
        // do nothing by default
    }

    /**
     * Called when a node is clicked/selected in the UI.
     * @param node Node that has been clicked.
     */
    public void onNodeClicked(Node node) {
        // do nothing by default
    }

    /**
     * Called when an edge is clicked/selected in the UI.
     * @param edge Edge that has been clicked.
     */
    public void onEdgeClicked(Edge edge) {
        // do nothing by default
    }
}
