package nl.rug.oop.rts.util;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Minimal hand-written JSON writer that manages indentation, commas and string
 * escaping. It exposes a small set of methods to open and close objects and
 * arrays and to write key-value fields, producing nicely indented JSON without
 * relying on any external library.
 */
public class JsonBuilder {
    /**
     * Indentation written for each nesting level.
     */
    private static final String INDENT = "  ";

    /**
     * Double quote character used to wrap JSON strings.
     */
    private static final String QUOTE = "\"";

    /**
     * Buffer that accumulates the JSON text as it is built.
     */
    private final StringBuilder sb = new StringBuilder();

    /**
     * Current nesting depth, controlling how much indentation is written.
     */
    private int depth = 0;

    /**
     * For each currently open container, whether it already holds a child.
     */
    private final Deque<Boolean> hasChild = new ArrayDeque<>();

    /**
     * Prepares the buffer to receive the next child of the current container by
     * writing a separating comma (if needed), a newline and the indentation.
     * Does nothing while writing the root element, which has no enclosing
     * container.
     */
    private void prepare() {
        if (!hasChild.isEmpty()) {
            if (hasChild.pop()) {
                sb.append(",");
            }
            hasChild.push(true);
            sb.append("\n");
            appendIndent();
        }
    }

    /**
     * Appends the indentation matching the current nesting depth.
     */
    private void appendIndent() {
        for (int i = 0; i < depth; i++) {
            sb.append(INDENT);
        }
    }

    /**
     * Writes a JSON string: an escaped value wrapped in double quotes.
     *
     * @param value the raw string to write, may be null
     */
    private void writeQuoted(String value) {
        sb.append(QUOTE).append(escape(value)).append(QUOTE);
    }

    /**
     * Writes a quoted key followed by the {@code ": "} separator.
     *
     * @param key the key to write
     */
    private void writeKey(String key) {
        writeQuoted(key);
        sb.append(": ");
    }

    /**
     * Opens a keyless object, used for the root element and for objects that are
     * items inside an array.
     */
    public void beginObject() {
        prepare();
        sb.append("{");
        depth++;
        hasChild.push(false);
    }

    /**
     * Closes the most recently opened object, placing the closing brace on its
     * own line when the object is non-empty.
     */
    public void endObject() {
        depth--;
        if (hasChild.pop()) {
            sb.append("\n");
            appendIndent();
        }
        sb.append("}");
    }

    /**
     * Opens a keyed array of the form {@code "key": [ ... ]}.
     *
     * @param key the key under which the array is stored
     */
    public void beginArray(String key) {
        prepare();
        writeKey(key);
        sb.append("[");
        depth++;
        hasChild.push(false);
    }

    /**
     * Closes the most recently opened array, placing the closing bracket on its
     * own line when the array is non-empty.
     */
    public void endArray() {
        depth--;
        if (hasChild.pop()) {
            sb.append("\n");
            appendIndent();
        }
        sb.append("]");
    }

    /**
     * Writes a string field of the form {@code "key": "value"}.
     *
     * @param key   the field key
     * @param value the field value, may be null
     */
    public void field(String key, String value) {
        prepare();
        writeKey(key);
        writeQuoted(value);
    }

    /**
     * Writes a numeric field of the form {@code "key": value}.
     *
     * @param key   the field key
     * @param value the field value
     */
    public void field(String key, int value) {
        prepare();
        writeKey(key);
        sb.append(value);
    }

    /**
     * Writes a bare string element inside an array.
     *
     * @param value the string value to write, may be null
     */
    public void value(String value) {
        prepare();
        writeQuoted(value);
    }

    /**
     * Escapes the characters that are not allowed to appear literally inside a
     * JSON string.
     *
     * @param value the raw string to escape, may be null
     * @return the escaped string, or an empty string when {@code value} is null
     */
    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Returns the JSON text accumulated so far.
     *
     * @return the built JSON string
     */
    @Override
    public String toString() {
        return sb.toString();
    }
}
