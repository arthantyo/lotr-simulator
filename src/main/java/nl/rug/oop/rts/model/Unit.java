package nl.rug.oop.rts.model;

import lombok.Getter;

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
     * Short background text for this unit.
     */
    private String history;

    /**
     * Creates a unit with the given name and combat stats.
     *
     * @param name display name of the unit
     * @param damage damage value of the unit
     * @param health health value of the unit
     * @param ability ability of the unit
     * @param history history of the unit
     */
    public Unit(String name, int damage, int health, String ability, String history) {
        this.name = name;
        this.damage = damage;
        this.health = health;
        this.ability = ability;
        this.history = history;
    }

}
