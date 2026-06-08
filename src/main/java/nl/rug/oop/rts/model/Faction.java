package nl.rug.oop.rts.model;

/**
 * Available factions in the simulation and the team each faction belongs to.
 */
@lombok.AllArgsConstructor
@lombok.Getter
public enum Faction {
    MEN(Team.GOOD, "Men"),
    ELVES(Team.GOOD, "Elves"),
    DWARVES(Team.GOOD, "Dwarves"),
    MORDOR(Team.EVIL, "Mordor"),
    ISENGARD(Team.EVIL, "Isengard"),;

    /**
     * Team associated with this faction.
     */
    private final Team team;

    private String name;

}
