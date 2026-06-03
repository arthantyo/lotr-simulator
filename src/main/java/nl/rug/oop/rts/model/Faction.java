package nl.rug.oop.rts.model;

/**
 * Available factions in the simulation and the team each faction belongs to.
 */
public enum Faction {
    MEN(Team.GOOD),
    ELVES(Team.GOOD),
    DWARVES(Team.GOOD),
    MORDOR(Team.EVIL),
    ISENGARD(Team.EVIL);

    /**
     * Team associated with this faction.
     */
    private final Team team;

    /**
     * Creates a faction value assigned to a team.
     *
     * @param team team this faction belongs to
     */
    Faction(Team team) {
        this.team = team;
    }

    /**
     * Returns the team this faction belongs to.
     *
     * @return faction team
     */
    public Team getTeam() {
        return team;
    }
}
