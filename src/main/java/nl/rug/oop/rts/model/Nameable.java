package nl.rug.oop.rts.model;

/**
 * Represents a graph element that has an editable name.
 */
public interface Nameable {
    /**
     * Returns the current name.
     *
     * @return the name
     */
    String getName();

    /**
     * Sets a new name.
     *
     * @param name the new name
     */
    void setName(String name);
}
