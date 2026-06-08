package nl.rug.oop.rts.model;

import java.util.ArrayList;

/**
 * Event that adds extra units to an army.
 */
public class ReinforcementsEvent implements Event {
    /**
     * Gets the display name of this event.
     *
     * @return the name of the event
     */
    @Override
    public String getName() {
        return "Reinforcements";
    }

    /**
     * Applies the reinforcement event to the given army.
     * This adds five new units to the army.
     *
     * @param army the army that receives reinforcements
     * @return a message describing what happened
     */
    @Override
    public String apply(Army army) {
        for (int i = 0; i < 5; i++) {
            army.getUnits().add(new Unit(
                    "Reinforcement",
                    10,
                    30,
                    "Fresh troops",
                    new ArrayList<>()));
        }

        return army.getFaction() + " received 5 reinforcement units.";
    }

    /**
     * Returns the event name when this event is displayed in a UI component,
     * such as a JOptionPane dropdown.
     *
     * @return the display name of the event
     */
    @Override
    public String toString() {
        return getName();
    }
}