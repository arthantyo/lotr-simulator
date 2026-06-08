package nl.rug.oop.rts.model;

/**
 * Represents an event that can affect an army during the simulation.
 */
public interface Event {
    /**
     * Gets the name of the event.
     *
     * @return event name
     */
    String getName();

    /**
     * Applies the event's effect to the given army and returns a description of the outcome.
     *
     * @param army army affectes by the event
     * @return message describing the outcome of the event on the army
     */
    String apply(Army army);

}
