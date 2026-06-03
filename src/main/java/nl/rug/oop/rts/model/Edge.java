package nl.rug.oop.rts.model;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a directed connection between two {@link Node} instances in the graph.
 * Contains an immutable id and name and mutable endpoints (`from` and `to`).
 */
public class Edge implements BattleLocation, Nameable {
    /**
     * Unique identifier of the edge.
     */
    @Getter
    private final int id;

    /**
     * Editable display name of the edge.
     */
    @Getter
    @Setter
    private  String name;

    /**
     * First node connected by this edge.
     */
    @Getter
    @Setter
    private Node from;

    /**
     * Second node connected by this edge.
     */
    @Getter
    @Setter
    private Node to;

    /**
     * Armies currently located on this edge.
     */
    @Getter
    private ArrayList<Army> armies = new ArrayList<>();

    /**
     * Creates an edge connecting two nodes.
     *
     * @param id   ID of the edge. Must be unique within the graph.
     * @param name Name of the edge.
     * @param from Node from which the edge starts. Must be a node that exists in the graph.
     * @param to   Node to which the edge points. Must be a node that exists in the graph.
     */
    public Edge(int id, String name, Node from, Node to) {
        this.id = id;
        this.name = name;
        this.from = from;
        this.to = to;
    }

}
