package nl.rug.oop.rts.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



public class BattleResolver {

    public BattleResult resolve(ArrayList<Army> armies) {

        // 1. Group units by team
        Map<Team, ArrayList<Unit>> unitsByTeam;
        unitsByTeam = new HashMap<>();

        for (Army army : armies) {
            unitsByTeam
                .computeIfAbsent(army.getTeam(), t -> new ArrayList<>())
                .addAll(army.getUnits());
        }

        Map<Team, Integer> powerByTeam = new HashMap<>();

        for (Map.Entry<Team, ArrayList<Unit>> entry : unitsByTeam.entrySet()) {

            int totalPower = 0;

            for (Unit u : entry.getValue()) {
                totalPower += u.getHealth() + u.getDamage();
            }

            powerByTeam.put(entry.getKey(), totalPower);
        }

        Team winner = Collections.max(
            powerByTeam.entrySet(),
            Map.Entry.comparingByValue()
        ).getKey();

        // 4. Build damage map (who should take damage)
        Map<Unit, Integer> damageMap = new HashMap<>();

        for (Map.Entry<Team,ArrayList<Unit>> entry : unitsByTeam.entrySet()) {

            Team team = entry.getKey();
            ArrayList<Unit> units = entry.getValue();

            for (Unit u : units) {

                if (team == winner) {
                    // winners take small damage
                    damageMap.put(u, u.getHealth() / 5);
                } else {
                    // losers take full damage
                    damageMap.put(u, u.getHealth());
                }
            }
        }

        // 5. Optional: kill attribution (simple version)
        Map<Unit, Integer> killMap = new HashMap<>();

        for (Team team : unitsByTeam.keySet()) {

            if (team == winner) {

                for (Unit u : unitsByTeam.get(team)) {
                    killMap.put(u, 1); // simple: 1 kill per unit (can be improved later)
                }
            }
        }

        return new BattleResult(winner, damageMap, killMap);
    }
}