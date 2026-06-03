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
    private final Team winner;

    private final Map<Unit, Integer> damageMap;

    private final Map<Unit, Integer> killMap;
}