package nl.rug.oop.rts.model;

import java.util.ArrayList;

import lombok.Getter;

/**
 * Represents a group of units belonging to one faction.
 */
@Getter
public class Army {
    /**
     * Faction this army belongs to.
     */
    private Faction faction;

    /**
     * Units contained in this army.
     */
    private ArrayList<Unit> units;

    /**
     * Creates an army for the given faction with the supplied units.
     *
     * @param faction faction of the army
     * @param units units that make up the army
     */
    public Army(Faction faction, ArrayList<Unit> units) {
        this.faction = faction;
        this.units = units;
    }

    /**
     * Returns the team associated with this army's faction.
     *
     * @return the team of the army
     */
    public Team getTeam() {
        return faction.getTeam();
    }

    public int getStrength() {
        return units.stream()
                .filter(u -> !u.isDead())
                .mapToInt(Unit::getPower)
                .sum();
    }

    public void removeDeadUnits() {
        units.removeIf(Unit::isDead);
    }
}
