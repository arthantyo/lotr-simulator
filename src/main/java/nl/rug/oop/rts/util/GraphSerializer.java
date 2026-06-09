package nl.rug.oop.rts.util;

import nl.rug.oop.rts.model.Army;
import nl.rug.oop.rts.model.Edge;
import nl.rug.oop.rts.model.Event;
import nl.rug.oop.rts.model.Graph;
import nl.rug.oop.rts.model.Node;
import nl.rug.oop.rts.model.Unit;

/**
 * Serialises a {@link Graph} and all of its contents into the JSON format
 * described by the assignment, using a {@link JsonBuilder} to handle the
 * low-level formatting.
 */
public class GraphSerializer {

    /**
     * Serialises the given graph, including all nodes, edges, armies, units and
     * events, into a JSON string.
     *
     * @param graph the graph to serialise
     * @return the JSON representation of the graph
     */
    public String serialize(Graph graph) {
        JsonBuilder builder = new JsonBuilder();
        builder.beginObject();
        builder.beginArray("Nodes");
        for (Node node : graph.getNodes()) {
            writeNode(builder, node);
        }
        builder.endArray();
        builder.beginArray("Edges");
        for (Edge edge : graph.getEdges()) {
            writeEdge(builder, edge);
        }
        builder.endArray();
        builder.endObject();
        return builder.toString();
    }

    /**
     * Writes a single node, including its armies and events.
     *
     * @param builder the builder to write to
     * @param node    the node to write
     */
    private void writeNode(JsonBuilder builder, Node node) {
        builder.beginObject();
        builder.field("Id", node.getId());
        builder.field("Name", node.getName());
        writeArmies(builder, node.getArmies());
        writeEvents(builder, node.getEvents());
        builder.endObject();
    }

    /**
     * Writes a single edge, including its endpoints, armies and events.
     *
     * @param builder the builder to write to
     * @param edge    the edge to write
     */
    private void writeEdge(JsonBuilder builder, Edge edge) {
        builder.beginObject();
        builder.field("Id", edge.getId());
        builder.field("Name", edge.getName());
        builder.field("Node1", edge.getFrom().getId());
        builder.field("Node2", edge.getTo().getId());
        writeArmies(builder, edge.getArmies());
        writeEvents(builder, edge.getEvents());
        builder.endObject();
    }

    /**
     * Writes an {@code "Armies"} array containing the given armies.
     *
     * @param builder the builder to write to
     * @param armies  the armies to write
     */
    private void writeArmies(JsonBuilder builder, Iterable<Army> armies) {
        builder.beginArray("Armies");
        for (Army army : armies) {
            writeArmy(builder, army);
        }
        builder.endArray();
    }

    /**
     * Writes a single army, including its faction, team and units.
     *
     * @param builder the builder to write to
     * @param army    the army to write
     */
    private void writeArmy(JsonBuilder builder, Army army) {
        builder.beginObject();
        builder.field("Faction", army.getFaction().getName());
        builder.field("Team", army.getTeam().getId());
        builder.beginArray("Units");
        for (Unit unit : army.getUnits()) {
            writeUnit(builder, unit);
        }
        builder.endArray();
        builder.endObject();
    }

    /**
     * Writes a single unit with its name, strength and health.
     *
     * @param builder the builder to write to
     * @param unit    the unit to write
     */
    private void writeUnit(JsonBuilder builder, Unit unit) {
        builder.beginObject();
        builder.field("Name", unit.getName());
        builder.field("Strength", unit.getDamage());
        builder.field("Health", unit.getHealth());
        builder.endObject();
    }

    /**
     * Writes an {@code "Events"} array containing the name of each event.
     *
     * @param builder the builder to write to
     * @param events  the events to write
     */
    private void writeEvents(JsonBuilder builder, Iterable<Event> events) {
        builder.beginArray("Events");
        for (Event event : events) {
            builder.value(event.getName());
        }
        builder.endArray();
    }
}
