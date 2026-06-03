package nl.rug.oop.rts.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the logic of resolving battles.
 */
public class BattleResolver {
    /**
     * Resolves a battle between the given armies and returns the result.
     *
     * @param armies the armies participating in the battle
     * @return the result of the battle, including the winner and damage/kill attribution
     */
    public BattleResult resolve(ArrayList<Army> armies) {
        Map<Team, ArrayList<Unit>> unitsByTeam = groupUnitsByTeam(armies);
        Map<Team, Integer> powerByTeam = calculatePowerByTeam(unitsByTeam);
        Team winner = determineWinner(powerByTeam);
        Map<Unit, Integer> damageMap = buildDamageMap(unitsByTeam, winner);
        Map<Unit, Integer> killMap = buildKillMap(unitsByTeam, winner);

        return new BattleResult(winner, damageMap, killMap);
    }

    /**
     * Groups all units from the given armies by their {@link Team}.
     *
     * @param armies the armies whose units are to be grouped
     * @return a map of each {@link Team} to the list of {@link Unit}s belonging to it
     */
    private Map<Team, ArrayList<Unit>> groupUnitsByTeam(ArrayList<Army> armies) {
        Map<Team, ArrayList<Unit>> unitsByTeam = new HashMap<>();

        for (Army army : armies) {
            unitsByTeam
                .computeIfAbsent(army.getTeam(), t -> new ArrayList<>())
                .addAll(army.getUnits());
        }

        return unitsByTeam;
    }

    /**
     * Calculates the total combat power for each {@link Team}.
     * Power is defined as the sum of each unit's health and damage values.
     *
     * @param unitsByTeam a map of each {@link Team} to its {@link Unit}s
     * @return a map of each {@link Team} to its total power score
     */
    private Map<Team, Integer> calculatePowerByTeam(Map<Team, ArrayList<Unit>> unitsByTeam) {
        Map<Team, Integer> powerByTeam = new HashMap<>();

        for (Map.Entry<Team, ArrayList<Unit>> entry : unitsByTeam.entrySet()) {
            int totalPower = 0;

            for (Unit unit : entry.getValue()) {
                totalPower += unit.getHealth() + unit.getDamage();
            }

            powerByTeam.put(entry.getKey(), totalPower);
        }

        return powerByTeam;
    }

    /**
     * Determines the winning {@link Team} as the one with the highest total power.
     *
     * @param powerByTeam a map of each {@link Team} to its total power score
     * @return the {@link Team} with the greatest power value
     */
    private Team determineWinner(Map<Team, Integer> powerByTeam) {
        return Collections.max(
            powerByTeam.entrySet(),
            Map.Entry.comparingByValue()
        ).getKey();
    }

    /**
     * Builds a damage map assigning how much damage each {@link Unit} should receive.
     * <ul>
     *   <li>Winners take 20% of their max health as damage.</li>
     *   <li>Losers take damage equal to their full health (lethal).</li>
     * </ul>
     *
     * @param unitsByTeam a map of each {@link Team} to its {@link Unit}s
     * @param winner      the {@link Team} that won the battle
     * @return a map of each {@link Unit} to the damage it should receive
     */
    private Map<Unit, Integer> buildDamageMap(Map<Team, ArrayList<Unit>> unitsByTeam, Team winner) {
        Map<Unit, Integer> damageMap = new HashMap<>();

        for (Map.Entry<Team, ArrayList<Unit>> entry : unitsByTeam.entrySet()) {
            Team team = entry.getKey();

            for (Unit unit : entry.getValue()) {
                int damage = (team == winner)
                    ? unit.getHealth() / 5   // winners take small damage
                    : unit.getHealth();       // losers take full damage

                damageMap.put(unit, damage);
            }
        }

        return damageMap;
    }

    /**
     * Builds a kill attribution map, assigning one kill to each {@link Unit}
     * on the winning {@link Team}.
     *
     * @param unitsByTeam a map of each {@link Team} to its {@link Unit}s
     * @param winner      the {@link Team} that won the battle
     * @return a map of each winning {@link Unit} to its kill count
     */
    private Map<Unit, Integer> buildKillMap(Map<Team, ArrayList<Unit>> unitsByTeam, Team winner) {
        Map<Unit, Integer> killMap = new HashMap<>();

        for (Unit unit : unitsByTeam.get(winner)) {
            killMap.put(unit, 1); // simple: 1 kill per unit (can be improved later)
        }

        return killMap;
    }
}