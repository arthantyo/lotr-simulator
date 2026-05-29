package nl.rug.oop.rts.model;

import java.util.ArrayList;

import nl.rug.oop.rts.view.GraphListener;

/**
 * Observable graph model that contains nodes and edges and notifies
 * registered {@link GraphListener} instances about changes and interactions.
 */
public class Graph {
    /** List of nodes in the graph. */
    private ArrayList<Node> nodes;
    /** List of edges in the graph. */
    private ArrayList<Edge> edges;
    /** List of listeners observing the graph. */
    private ArrayList<GraphListener> observables;

    /**
     * Constructor for Graph. Add more details here.
     * @param nodes List of nodes in the graph.
     * @param edges List of edges in the graph.
     */
    public Graph(ArrayList<Node> nodes, ArrayList<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
        this.observables = new ArrayList<>();
    }

    /**
     * Add a listener to the graph. Add more details here.
     * @param listener Listener to add.
     */
    public void addListener(GraphListener listener) {
        this.observables.add(listener);
    }

    /**
     * Remove a listener from the graph. Add more details here.
     * @param listener Listener to remove.
     */
    public void removeListener(GraphListener listener) {
        this.observables.remove(listener);
    }
    
    /**
     * Notify all listeners that a node has been added. Add more details here. 
     * @param node Node that has been added.
     */
    public void notifyNodeAdded(Node node) {
        for (GraphListener listener : this.observables) {
            listener.onNodeAdded(node);
        }
    }

    /**
     *  Notify all listeners that a node has been deleted. Add more details here.
     * @param node Node that has been deleted.
     */
    public void notifyNodeDeleted(Node node) {
        for (GraphListener listener : this.observables) {
            listener.onNodeDeleted(node);
        }
    }

    /**
     * Notify all listeners that an edge has been added. Add more details here.
     * @param edge Edge that has been added.
     */
    public void notifyEdgeAdded(Edge edge) {
        for (GraphListener listener : this.observables) {
            listener.onEdgeAdded(edge);
        }
    }

    /**
     * Notify all listeners that an edge has been deleted. Add more details here.
     * @param edge Edge that has been deleted.
     */
    public void notifyEdgeDeleted(Edge edge) {
        for (GraphListener listener : this.observables) {
            listener.onEdgeDeleted(edge);
        }
    }

    /**
     * Notify all listeners that a node has been clicked. Add more details here.
     * @param node Node that has been clicked.
     */
    public void notifyNodeClicked(Node node) {
        for (GraphListener listener : this.observables) {
            listener.onNodeClicked(node);
        }
    }

    /**
     * Notify all listeners that an edge has been clicked. Add more details here.
     * @param edge Edge that has been clicked.
     */
    public void notifyEdgeClicked(Edge edge) {
        for (GraphListener listener : this.observables) {
            listener.onEdgeClicked(edge);
        }
    }

    /**
     * Add a node to the graph. Add more details here.
     * @param node Node to add.
     */
    public void addNode(Node node) {
        this.nodes.add(node);
    }

    /**
     * Delete a node from the graph. Add more details here.
     * @param id ID of the node to delete.
     * @return The deleted node, or null if no node with the given ID was found.
     */
    public Node deleteNode(int id) {
        this.nodes.removeIf(node -> node.getId() == id);

        // also delete all edges connected to this node
        this.edges.removeIf(edge -> edge.getFrom().getId() == id || edge.getTo().getId() == id);

        this.notifyNodeDeleted(new Node(id, ""));

        return null;
    }

    /**
     * Get a node from the graph. Add more details here.
     * @param id ID of the node to get.
     * @return The node with the given ID, or null if no node with the given ID was found.
     */
    public Node getNode(int id) {
        Node target = this.nodes.stream().filter(node -> node.getId() == id).findFirst().orElse(null);

        this.notifyNodeClicked(target);

        return target;
    }

    /**
     * Add an edge to the graph. Add more details here.
     * @param edge Edge to add. The edge must connect two existing nodes in the graph.
     */
    public void addEdge(Edge edge) {
        // edge must connect two existing nodes
        if (!this.nodes.contains(edge.getFrom()) || !this.nodes.contains(edge.getTo())) {
            throw new IllegalArgumentException("Edge must connect two existing nodes");
        }

        this.edges.add(edge);

        this.notifyEdgeAdded(edge);
    }

    /**
     * Delete an edge from the graph. Add more details here.
     * @param id ID of the edge to delete.
     * @return The deleted edge, or null if no edge with the given ID was found.
     */
    public Edge deleteEdge(int id) {
        this.edges.removeIf(edge -> edge.getId() == id);

        this.notifyEdgeDeleted(new Edge(id, "", null, null));

        return null;
    }

    /**
     * Get an edge from the graph. Add more details here.
     * @param id ID of the edge to get.
     * @return The edge with the given ID, or null if no edge with the given ID was found.
     */
    public Edge getEdge(int id) {
        Edge target = this.edges.stream().filter(edge -> edge.getId() == id).findFirst().orElse(null);

        this.notifyEdgeClicked(target);
        
        return target;
    }

    /**
     * Returns the list of nodes.
     * @return list of nodes
     */
    public ArrayList<Node> getNodes() {
        return nodes;
    }

    /**
     * Returns the list of edges.
     * @return list of edges
     */
    public ArrayList<Edge> getEdges() {
        return edges;
    }

    /**
     * Returns the registered listeners.
     * @return list of observers
     */
    public ArrayList<GraphListener> getObservables() {
        return observables;
    }
}
