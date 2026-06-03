package nl.rug.oop.rts.model;

import java.util.ArrayList;

/**
 * Represents a battle between two or more armies.
 */
public class Battle {
    /**
     * Armies participating in this battle.
     */
    private final ArrayList<Army> armies;
    /**
     * The resolver for handling battle logic.
     */
    private final BattleResolver resolver = new BattleResolver();
    /**
     * The executor for carrying out battle actions.
     */
    private final BattleExecutor executor = new BattleExecutor();

    /**
     * Creates a battle with the given armies.
     * @param armies the armies participating in the battle
     */
    public Battle(ArrayList<Army> armies) {
        this.armies = armies;
    }

    /**
     * Resolves the battle and returns the result.
     * @return the result of the battle
     */
    public BattleResult resolve() {
        BattleResult result = resolver.resolve(armies);

        executor.execute(result, armies);

        return result;
    }
}