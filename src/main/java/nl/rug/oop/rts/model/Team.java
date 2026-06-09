package nl.rug.oop.rts.model;

/**
 * Represents the two opposing sides in the simulation.
 */
@lombok.AllArgsConstructor
@lombok.Getter
public enum Team {
    GOOD(1),
    EVIL(2);

    /**
     * Numeric identifier of the team, used when exporting to JSON.
     */
    private final int id;
}
