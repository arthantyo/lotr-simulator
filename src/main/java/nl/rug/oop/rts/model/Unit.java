package nl.rug.oop.rts.model;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents one unit type inside an army, including combat stats.
 */
@Getter
public class Unit {
    /**
     * Damage dealt by this unit.
     */
    private int damage;

    /**
     * Health of this unit.
     */
    private int health;

    /**
     * Display name of this unit.
     */
    private String name;

    /**
     * Short description of this unit's ability.
     */
    private String ability;

    /**
     * History actions of this unit.
     */
    @Setter
    private ArrayList<String> history;

    /**
     * Creates a unit with the given name and combat stats.
     *
     * @param name display name of the unit
     * @param damage damage value of the unit
     * @param health health value of the unit
     * @param ability ability of the unit
     * @param history history of the unit
     */
    public Unit(String name, int damage, int health, String ability, ArrayList<String> history) {
        this.name = name;
        this.damage = damage;
        this.health = health;
        this.ability = ability;
        this.history = history;
    }

    /**
     * Calculates the power of this unit based on its health and damage.
     * @return  the calculated power of the unit
     */
    public int getPower() {
        return health + damage;
    }

    /**
     * Applies damage to this unit, reducing its health by the specified amount.
     * @param amount the amount of damage to apply to the unit
     */
    public void damage(int amount) {
        health -= Math.max(0, amount);
    }

    /**
     * Checks if this unit is dead (health is 0 or below).
     * @return true if the unit is dead, false otherwise
     */
    public boolean isDead() {
        return health <= 0;
    }
}
