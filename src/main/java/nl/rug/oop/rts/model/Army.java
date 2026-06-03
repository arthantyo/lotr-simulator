package nl.rug.oop.rts.model;

import java.util.ArrayList;

import lombok.Getter;

@Getter
public class Army {
    private Faction faction;
    private ArrayList<Unit> units;

    public Army(Faction faction, ArrayList<Unit> units) {
        this.faction = faction;
        this.units = units;
    }

    public Team getTeam() {
        return faction.getTeam();
    }

}
