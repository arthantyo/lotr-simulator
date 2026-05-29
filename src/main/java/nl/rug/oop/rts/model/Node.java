package nl.rug.oop.rts.model;

import lombok.Getter;

public class Node {
    @Getter 
    private final int id;

    @Getter
    private final String name;

    public Node(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
