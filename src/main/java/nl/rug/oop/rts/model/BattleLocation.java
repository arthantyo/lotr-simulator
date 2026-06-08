package nl.rug.oop.rts.model;

import java.util.ArrayList;

/**
 * Represents a location where battles can take place.
 */
public interface BattleLocation {
    /**
     * Gets the name of this battle location, used for logging and history purposes.
     * 
     * @return the name of this battle location
     */
    String getName();

    /**
     * Gets the events that can occur at this location.
     *
     * @return list of events
     */
    ArrayList<Event> getEvents();
}
