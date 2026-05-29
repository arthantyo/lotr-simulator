package nl.rug.oop.rts.model;

import java.util.ArrayList;

public class Graph {
    public ArrayList<Node> nodes;
    public ArrayList<Edge> edges;


    public Graph(ArrayList<Node> nodes, ArrayList<Edge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }

    public void addNode(Node node) {
        this.nodes.add(node);
    }

    public Node deleteNode(int id) {
        this.nodes.removeIf(node -> node.getId() == id);

        // also delete all edges connected to this node
        this.edges.removeIf(edge -> edge.getFrom().getId() == id || edge.getTo().getId() == id);

        return null;
    }

    public Node getNode(int id) {
        return this.nodes.stream().filter(node -> node.getId() == id).findFirst().orElse(null);
    }
    // edge methods
    public void addEdge(Edge edge) {
        // edge must connect two existing nodes
        if (!this.nodes.contains(edge.getFrom()) || !this.nodes.contains(edge.getTo())) {
            throw new IllegalArgumentException("Edge must connect two existing nodes");
        }

        this.edges.add(edge);
    }

    public Edge deleteEdge(int id) {
        this.edges.removeIf(edge -> edge.getId() == id);
        return null;
    }

    public Edge getEdge(int id) {
        return this.edges.stream().filter(edge -> edge.getId() == id).findFirst().orElse(null);
    }
}
