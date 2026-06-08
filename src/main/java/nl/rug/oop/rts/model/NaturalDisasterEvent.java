package nl.rug.oop.rts.model;

/**
 * Event that remover units from an army.
 */
public class NaturalDisasterEvent implements Event {
    /**
     * Gets the dispaly name of the event.
     * 
     * @return the name of the event
     */
    @Override
    public String getName() {
        return "Natural Disaster";
    }

    /**
     * Applies the natural disaster event to the given army.
     * This removes up to five units from the army.
     * 
     * @param army the army affected by the disaster
     * @return a message describing what happend
     */
    @Override
    public String apply(Army army) {
        int removed = Math.min(5, army.getUnits().size());

        for (int i = 0; i < removed; i++) {
            army.getUnits().remove(army.getUnits().size() - 1);
        }
        return army.getFaction() + " lost " + removed + " units.";
    }

    /**
     * Returns the event name when displayd in UI components.
     * 
     * @return the dispplay of the event.
     */
    @Override
    public String toString() {
        return getName();
    }
}
