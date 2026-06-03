package nl.rug.oop.rts.model;


public enum Faction {
    MEN(Team.GOOD),
    ELVES(Team.GOOD),
    DWARVES(Team.GOOD),
    MORDOR(Team.EVIL),
    ISENGARD(Team.EVIL),

    private final Team team;

    Faction(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }
}
