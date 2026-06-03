package nl.rug.oop.rts.model;

import java.util.ArrayList;
import java.util.Map;

/**
 * Executes the results of a battle by applying damage and kills to the participating units.
 */
public class BattleExecutor {
    /**
     * Executes the results of a battle.
     * @param result the result of the battle, including damage and kills to apply
     * @param armies the armies that participated in the battle
     */
    public void execute(BattleResult result, ArrayList<Army> armies) {
        for (Map.Entry<Unit, Integer> entry : result.getDamageMap().entrySet()) {
            entry.getKey().damage(entry.getValue());
        }

        for (Map.Entry<Unit, Integer> entry : result.getKillMap().entrySet()) {
            entry.getKey().getHistory().add(
                "Participated in battle and achieved " + entry.getValue() + " kills"
            );
        }

        for (Army army : armies) {
            army.removeDeadUnits();
        }

        armies.removeIf(army -> army.getUnits().isEmpty());
    }
}