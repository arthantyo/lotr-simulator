package nl.rug.oop.rts.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import lombok.Getter;
import lombok.Setter;

/**
 * Observable graph model that contains nodes and edges and notifies.
 */
@Getter
@Setter
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
     * Currently selected node, or null if no node is selected.
     */
    private Node selectedNode = null;
    /**
     * Currently selected edge, or null if no edge is selected.
     */
    private Edge selectedEdge = null;
    /**
     * Counter used to hand out unique node ids.
     */
    private int nextNodeId = 0;
    /**
     * Counter used to hand out unique edge ids.
     */
    private int nextEdgeId = 0;

    /**
     * Creates an empty graph with no nodes or edges.
     */
    public Graph() {
        this.nodes = new ArrayList<Node>();
        this.edges = new ArrayList<Edge>();
    }

    /**
     * Returns the next unique node id and advances the counter.
     *
     * @return a unique node id
     */
    public int nextNodeId() {
        return nextNodeId++;
    }

    /**
     * Returns the next unique edge id and advances the counter.
     *
     * @return a unique edge id
     */
    public int nextEdgeId() {
        return nextEdgeId++;
    }

    /**
     * Registers a callback for a specific graph event.
     *
     * @param event    Name of the event to listen for.
     * @param callback Function to call when the event occurs.
     */
    public void addListener(GraphEventType event, Consumer<Object> callback) {
        listeners.computeIfAbsent(event, k -> new ArrayList<>()).add(callback);
    }

    /**
     * Removes a previously registered callback from a specific graph event.
     *
     * @param event    Name of the event for which to remove the listener.
     * @param callback Function to remove from the list of listeners for the
     *                 specified event.
     */
    public void removeListener(GraphEventType event, Consumer<Object> callback) {
        ArrayList<Consumer<Object>> eventListeners = listeners.get(event);
        if (eventListeners == null) {
            return;
        }

        eventListeners.remove(callback);

        if (eventListeners.isEmpty()) {
            listeners.remove(event);
        }
    }

    /**
     * Notifies all listeners registered for the given event.
     *
     * @param event Name of the event to emit. Listeners can subscribe to specific
     *              events by name.
     * @param data  Data associated with the event.
     */
    private void emit(GraphEventType event, Object data) {
        ArrayList<Consumer<Object>> eventListeners = listeners.get(event);
        if (eventListeners == null) {
            return;
        }

        for (Consumer<Object> listener : eventListeners) {
            listener.accept(data);
        }
    }

    /**
     * Adds a node to the graph and notifies listeners.
     *
     * @param node Node to add.
     */
    public void addNode(Node node) {
        this.nodes.add(node);
        emit(GraphEventType.NODE_ADDED, node);
    }

    /**
     * Removes a node and all edges connected to it from the graph.
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

        if (removed == selectedNode) {
            clearSelection();
        }

        return removed;
    }

    /**
     * Finds a node by its id.
     *
     * @param id ID of the node to get.
     * @return The node with the given ID, or null if no node with the given ID was
     *         found.
     */
    public Node getNode(int id) {
        Node target = this.nodes.stream().filter(node -> node.getId() == id).findFirst().orElse(null);

        return target;
    }

    /**
     * Adds an edge to the graph after verifying that both endpoints exist.
     *
     * @param edge Edge to add. The edge must connect two existing nodes in the
     *             graph.
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
     * Removes an edge from the graph.
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

        if (removed == selectedEdge) {
            clearSelection();
        }

        return removed;
    }

    /**
     * Finds an edge by its id.
     *
     * @param id ID of the edge to get.
     * @return The edge with the given ID, or null if no edge with the given ID was
     *         found.
     */
    public Edge getEdge(int id) {
        Edge target = this.edges.stream()
                .filter(edge -> edge.getId() == id)
                .findFirst()
                .orElse(null);

        return target;
    }

    /**
     * Selects the given node, deselecting any selected edge, and notifies
     * listeners.
     *
     * @param node the node to select
     */
    public void setSelectedNode(Node node) {
        this.selectedNode = node;
        this.selectedEdge = null;
        emit(GraphEventType.SELECTION_CHANGED, node);
    }

    /**
     * Selects the given edge, deselecting any selected node, and notifies
     * listeners.
     *
     * @param edge the edge to select
     */
    public void setSelectedEdge(Edge edge) {
        this.selectedEdge = edge;
        this.selectedNode = null;
        emit(GraphEventType.SELECTION_CHANGED, edge);
    }

    /**
     * Clears the current selection and notifies listeners.
     */
    public void clearSelection() {
        this.selectedNode = null;
        this.selectedEdge = null;
        emit(GraphEventType.SELECTION_CHANGED, null);
    }

    /**
     * Adds an army to a node and notifies listeners that army data changed.
     *
     * @param node node that receives the army
     * @param army army to add
     */
    public void addArmyToNode(Node node, Army army) {
        node.getArmies().add(army);
        emit(GraphEventType.ARMIES_CHANGED, node);
    }

    /**
     * Removes an army from a node and notifies listeners that army data changed.
     *
     * @param node node that contains the army
     * @param army army to remove
     */
    public void removeArmyFromNode(Node node, Army army) {
        node.getArmies().remove(army);
        emit(GraphEventType.ARMIES_CHANGED, node);

    }

    /**
     * Adds an army to an edge and notifies listeners that army data changed.
     *
     * @param edge edge that receives the army
     * @param army army to add
     */
    public void addArmyToEdge(Edge edge, Army army) {
        edge.getArmies().add(army);
        emit(GraphEventType.ARMIES_CHANGED, edge);
    }

    /**
     * Removes an army from an edge and notifies listeners that army data changed.
     *
     * @param edge edge that contains the army
     * @param army army to remove
     */
    public void removeArmyFromEdge(Edge edge, Army army) {
        edge.getArmies().remove(army);
        emit(GraphEventType.ARMIES_CHANGED, edge);
    }

    /**
     * Adds an event to a battle location and notifies listeners.
     *
     * @param location location that receives the event
     * @param event    event to add
     */
    public void addEventToLocation(BattleLocation location, Event event) {
        location.getEvents().add(event);
        emit(GraphEventType.EVENTS_CHANGED, location);
    }

    /**
     * Removes an event from a battle location and notifies listeners.
     *
     * @param location location that contains the event
     * @param event    event to remove
     */
    public void removeEventFromLocation(BattleLocation location, Event event) {
        location.getEvents().remove(event);
        emit(GraphEventType.EVENTS_CHANGED, location);
    }
}
