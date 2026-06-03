package nl.rug.oop.rts.model;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a directed connection between two {@link Node} instances in the graph.
 * Contains an immutable id and name and mutable endpoints (`from` and `to`).
 */
public class Edge {
    /**
     * ID of the edge. Add more details here.
     */
    @Getter
    private final int id;

    /**
     * Name of the edge. Add more details here.
     */
    @Getter
    @Setter
    private  String name;

    /**
     * Node from which the edge starts. Add more details here.
     */
    @Getter
    @Setter
    private Node from;

    /**
     * Node to which the edge points. Add more details here.
     */
    @Getter
    @Setter
    private Node to;

    @Getter
    private ArrayList<Army> armies = new ArrayList<>();

    /**
     * Constructor for Edge. Add more details here.
     *
     * @param id   ID of the edge. Must be unique within the graph.
     * @param name Name of the edge. Add more details here.
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
