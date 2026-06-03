package nl.rug.oop.rts.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class Unit {
    private int damage;
    private int health;
    private String name;

    public Unit(String name, int damage, int health) {
        this.name = name;
        this.damage = damage;
        this.health = health;
    }

}
