package nl.rug.oop.rts.model;

/**
 * Event that improves the damage of units in an army.
 */
public class HiddenWeaponryEvent implements Event {
    /**
     * Gets the dispaly name of this event.
     * 
     * @return the name of the event
     */
    @Override
    public String getName() {
        return "Hidden Weaponry";
    }

    /**
     * Applies the hidden weaponry event to the given army.
     * This increases the damage of every uint in the army.
     * 
     * @param army the army that finds the hidden weaponry.
     * @return a meassage describing what happend
     */
    @Override
    public String apply(Army army) {
        for (Unit unit : army.getUnits()) {
            unit.increaseDamage(5);
            unit.getHistory().add("Found hidden weaponry");
        }
        return army.getFaction() + " found hidden weaponry. Damage increased.";
    }

    /**
     * Returns the event name when displayed in UI components.
     * 
     * @return the dispaly=ay name of the event
     */
    @Override
    public String toString() {
        return getName();
    }

}
