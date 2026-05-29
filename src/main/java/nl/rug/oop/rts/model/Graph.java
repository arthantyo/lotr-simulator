package nl.rug.oop.rts.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Observable graph model that contains nodes and edges and notifies.
 */
public class Graph {
    /**
     * List of nodes in the graph.
     */
    private ArrayList<Node> nodes;
    /**
     * List of edges in the graph.
     */
    private ArrayList<Edge> edges;
    /**
     * List of listeners observing the graph.
     */
    private Map<GraphEventType, ArrayList<Consumer<Object>>> listeners = new HashMap<>();

    /**
     * Constructor for Graph. Add more details here.
     */
    public Graph() {
        this.nodes = new ArrayList<Node>();
        this.edges = new ArrayList<Edge>();
    }

    /**
     * Add a listener for a specific event. Add more details here.
     *
     * @param event    Name of the event to listen for. 
     * @param callback Function to call when the event occurs. 
     */
    public void addListener(GraphEventType event, Consumer<Object> callback) {
        listeners.computeIfAbsent(event, k -> new ArrayList<>()).add(callback);
    }

    /**
     * Remove a listener for a specific event. Add more details here.
     *
     * @param event    Name of the event for which to remove the listener.
     * @param callback Function to remove from the list of listeners for the specified event.
     */
    public void removeListener(GraphEventType event, Consumer<Object> callback) {
        ArrayList<Consumer<Object>> eventListeners = listeners.get(event);
        if (eventListeners == null){
            return;
        }

        eventListeners.remove(callback);

        if (eventListeners.isEmpty()) {
            listeners.remove(event);
        }
    }

    /**
     * Emit an event to all registered listeners. Add more details here.
     *
     * @param event Name of the event to emit. Listeners can subscribe to specific events by name.
     * @param data  Data associated with the event. 
     */
    private void emit(GraphEventType event, Object data) {
        ArrayList<Consumer<Object>> eventListeners = listeners.get(event);
        if (eventListeners == null){ 
            return;
        }

        for (Consumer<Object> listener : eventListeners) {
            listener.accept(data);
        }
    }

    /**
     * Add a node to the graph. Add more details here.
     *
     * @param node Node to add.
     */
    public void addNode(Node node) {
        this.nodes.add(node);
        emit(GraphEventType.NODE_ADDED, node);
    }

    /**
     * Delete a node from the graph. Add more details here.
     *
     * @param id ID of the node to delete.
     * @return The deleted node, or null if no node with the given ID was found.
     */
    public Node deleteNode(int id) {
        Node removed = this.nodes.stream()
                .filter(n -> n.getId() == id)
                .findFirst()
                .orElse(null);

        this.nodes.removeIf(node -> node.getId() == id);
        this.edges.removeIf(edge -> edge.getFrom().getId() == id || edge.getTo().getId() == id);

        emit(GraphEventType.NODE_DELETED, removed);

        return removed;
    }

    /**
     * Get a node from the graph. Add more details here.
     *
     * @param id ID of the node to get.
     * @return The node with the given ID, or null if no node with the given ID was found.
     */
    public Node getNode(int id) {
        Node target = this.nodes.stream().filter(node -> node.getId() == id).findFirst().orElse(null);

        emit(GraphEventType.NODE_CLICKED, target);

        return target;
    }

    /**
     * Add an edge to the graph. Add more details here.
     *
     * @param edge Edge to add. The edge must connect two existing nodes in the graph.
     */
    public void addEdge(Edge edge) {
        // edge must connect two existing nodes
        if (!this.nodes.contains(edge.getFrom()) || !this.nodes.contains(edge.getTo())) {
            throw new IllegalArgumentException("Edge must connect two existing nodes");
        }

        this.edges.add(edge);

        emit(GraphEventType.EDGE_ADDED, edge);
    }

    /**
     * Delete an edge from the graph. Add more details here.
     *
     * @param id ID of the edge to delete.
     * @return The deleted edge, or null if no edge with the given ID was found.
     */
    public Edge deleteEdge(int id) {
        Edge removed = this.edges.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);

        this.edges.removeIf(edge -> edge.getId() == id);

        emit(GraphEventType.EDGE_DELETED, removed);

        return removed;
    }

    /**
     * Get an edge from the graph. Add more details here.
     *
     * @param id ID of the edge to get.
     * @return The edge with the given ID, or null if no edge with the given ID was found.
     */
    public Edge getEdge(int id) {
        Edge target = this.edges.stream()
                .filter(edge -> edge.getId() == id)
                .findFirst()
                .orElse(null);

        emit(GraphEventType.EDGE_CLICKED, target);

        return target;
    }

    /**
     * Returns the list of nodes.
     *
     * @return list of nodes
     */
    public ArrayList<Node> getNodes() {
        return nodes;
    }

    /**
     * Returns the list of edges.
     *
     * @return list of edges
     */
    public ArrayList<Edge> getEdges() {
        return edges;
    }
}
