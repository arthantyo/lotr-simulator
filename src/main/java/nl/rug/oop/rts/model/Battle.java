package nl.rug.oop.rts.model;

import java.util.ArrayList;

public class Battle {

    private final ArrayList<Army> armies;
    private final BattleResolver resolver = new BattleResolver();
    private final BattleExecutor executor = new BattleExecutor();

    public Battle(ArrayList<Army> armies) {
        this.armies = armies;
    }

    public BattleResult resolve() {
        BattleResult result = resolver.resolve(armies);

        executor.execute(result, armies);

        return result;
    }
}