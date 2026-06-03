package nl.rug.oop.rts.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;


/**
 * Represents a node (vertex) in the graph with an immutable id and an editable
 * name.
 */
@Getter
@Setter
public class Node implements BattleLocation {
    /**
     * Unique identifier of the node.
     */
    private final int id;

    /**
     * Editable display name of the node.
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

    /**
     * Armies currently located on this node.
     */
    private ArrayList<Army> armies = new ArrayList<>();

    /**
     * Creates a node at the given position with an empty army list.
     *
     * @param id unique identifier of the node
     * @param name display name of the node
     * @param x x coordinate on the map
     * @param y y coordinate on the map
     */
    public Node(int id, String name, int x, int y) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.armies = new ArrayList<>();
    }
}
