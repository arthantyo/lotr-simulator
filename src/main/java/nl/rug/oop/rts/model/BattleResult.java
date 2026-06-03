package nl.rug.oop.rts.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represents the result of a battle, including the winning and losing teams,
 * as well as the damage dealt and kills made by each unit.
 */
@Getter
@AllArgsConstructor
public class BattleResult {
    /**
     * The team that won the battle.
     */
    private final Team winner;

    /**
     * Map of units to the damage they dealt in this battle. 
     */
    private final Map<Unit, Integer> damageMap;

    /**
     * Map of units to the number of kills they achieved in this battle. Used for
     */
    private final Map<Unit, Integer> killMap;
}