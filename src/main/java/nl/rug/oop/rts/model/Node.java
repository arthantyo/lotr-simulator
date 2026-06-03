package nl.rug.oop.rts.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

import lombok.AllArgsConstructor;

/**
 * Represents a node (vertex) in the graph with an immutable id and an editable
 * name.
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
    private String name;

    /**
     * X coordinate of the node on the panel.
     */
    private int x;

    /**
     * Y coordinate of the node on the panel.
     */
    private int y;

    private ArrayList<Army> armies = new ArrayList<>();

    public Node(int id, String name, int x, int y) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.armies = new ArrayList<>();
    }
}
