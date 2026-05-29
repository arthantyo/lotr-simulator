package nl.rug.oop.rts.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a node (vertex) in the graph with an immutable id and name.
 */
@Getter
@Setter
public class Node {
    /**
     * ID of the node. Add more details here.
     */
    private final int id;

    /**
     * Name of the node. Add more details here.
     */
    private final String name;

    private int x;
    private int y;


    /**
     * Constructor for Node. Add more details here.
     *
     * @param id   ID of the node. Must be unique within the graph.
     * @param name Name of the node. Add more details here.
     */
    public Node(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
