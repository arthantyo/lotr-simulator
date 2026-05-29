package nl.rug.oop.rts.model;

import lombok.Getter;
import lombok.Setter;

public class Edge {
    @Getter
    private final int id; 

    @Getter
    private final String name;

    @Getter
    @Setter
    private Node from;

    @Getter
    @Setter
    private Node to;

    public Edge(int id, String name, Node from, Node to) {
        this.id = id;
        this.name = name;
        this.from = from;
        this.to = to;
    }

}
