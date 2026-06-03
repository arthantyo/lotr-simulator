package nl.rug.oop.rts.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import lombok.Getter;

/**
 * Manages the state and progression of the simulation.
 */
public class Simulation {

    /** The initial graph state. */
    private final Graph initialGraph;

    /**
     * Current time step of the simulation.
     */
    @Getter
    private int timeStep = 0; 

    /**
     * Event chance.
     */
    private static final int EVENT_CHANCE_PERCENT = 10;

    /**
     * History of graph states for stepping back in time.
     */
    private final Deque<Graph> history = new ArrayDeque<>();

    /**
     * Constructor for Simulation. Add more details here.
     * @param graph The initial graph state for the simulation. Must not be null.
     */
    public Simulation(Graph graph) {
        initialGraph = graph;
    }

    /**
     * Reverts the simulation to the previous time step.
     * @param graph  The graph to revert. Must not be null.
     */
    public void subtractTime(Graph graph) {
        if (history.isEmpty()) {
            System.out.println("No previous states to revert to.");
            return;
        }

        Graph previousState = history.pop();
        graph.setNodes(previousState.getNodes());
        graph.setEdges(previousState.getEdges());
        timeStep--;
        System.out.println("Reverted simulation to time step " + timeStep);
    }

    /**
     * Advances the simulation by one time step, moving armies and resolving battles and events.
     *
     * @param graph The graph to advance.
     */
    public void advanceTime(Graph graph) {
        history.push(graph);
        System.out.println("Advancing simulation to time step " + (timeStep + 1));
        timeStep++;

        Map<Army, Node> armiesOnNodes = snapshotArmiesOnNodes(graph);
        Map<Army, Edge> armiesOnEdges = snapshotArmiesOnEdges(graph);
        Map<Node, List<Edge>> neighboringEdges = buildNeighboringEdges(graph.getEdges());

        processArmiesOnNodes(armiesOnNodes, neighboringEdges);
        processArmiesOnEdges(armiesOnEdges);
    }

    /**
     * Snapshots all armies currently stationed on nodes.
     *
     * @param graph The graph containing nodes to snapshot.
     * @return A map of each {@link Army} to the {@link Node} it currently occupies.
     */
    private Map<Army, Node> snapshotArmiesOnNodes(Graph graph) {
        Map<Army, Node> armiesOnNodes = new HashMap<>();
        for (Node node : graph.getNodes()) {
            for (Army army : node.getArmies()) {
                armiesOnNodes.put(army, node);
            }
        }
        return armiesOnNodes;
    }

    /**
     * Snapshots all armies currently stationed on edges.
     *
     * @param graph The graph containing edges to snapshot.
     * @return A map of each {@link Army} to the {@link Edge} it currently occupies.
     */
    private Map<Army, Edge> snapshotArmiesOnEdges(Graph graph) {
        Map<Army, Edge> armiesOnEdges = new HashMap<>();
        for (Edge edge : graph.getEdges()) {
            for (Army army : edge.getArmies()) {
                armiesOnEdges.put(army, edge);
            }
        }
        return armiesOnEdges;
    }

    /**
     * Processes all armies on nodes: resolves battles, moves each army to a random
     * neighboring edge, and triggers random events.
     *
     * @param armiesOnNodes   A snapshot map of armies to their current nodes.
     * @param neighboringEdges A map of each {@link Node} to its adjacent {@link Edge}s.
     */
    private void processArmiesOnNodes(Map<Army, Node> armiesOnNodes, Map<Node, List<Edge>> neighboringEdges) {
        for (Army army : new ArrayList<>(armiesOnNodes.keySet())) {
            Node source = armiesOnNodes.get(army);
            if (!isArmyValidOnNode(army, source)) {
                continue;
            }

            if (isDifferentTeam(source.getArmies())) {
                startNodeBattle(source);
            }

            List<Edge> edges = neighboringEdges.get(source);
            if (edges == null || edges.isEmpty()) {
                continue;
            }

            moveArmyFromNodeToEdge(army, source, edges);
            maybeCreateRandomEvent();
        }
    }

    /**
     * Returns whether an army is still valid on a given node, i.e. the node is
     * non-null and still contains the army.
     *
     * @param army   The army to validate.
     * @param source The node to check.
     * @return {@code true} if the army is present on the node; {@code false} otherwise.
     */
    private boolean isArmyValidOnNode(Army army, Node source) {
        return source != null && source.getArmies().contains(army);
    }

    /**
     * Moves an army from a node to a randomly selected neighboring edge,
     * then resolves any resulting battle on that edge.
     *
     * @param army   The army to move.
     * @param source The node the army is departing from.
     * @param edges  The list of neighboring edges to choose from.
     */
    private void moveArmyFromNodeToEdge(Army army, Node source, List<Edge> edges) {
        int randomEdgeIndex = ThreadLocalRandom.current().nextInt(edges.size());
        Edge selectedEdge = edges.get(randomEdgeIndex);

        source.removeArmy(army);
        selectedEdge.addArmy(army);

        if (isDifferentTeam(selectedEdge.getArmies())) {
            startEdgeBattle(selectedEdge);
        }
    }

    /**
     * Processes all armies on edges: resolves battles, moves each army to a randomly
     * chosen endpoint node, and triggers random events.
     *
     * @param armiesOnEdges A snapshot map of armies to their current edges.
     */
    private void processArmiesOnEdges(Map<Army, Edge> armiesOnEdges) {
        for (Army army : new ArrayList<>(armiesOnEdges.keySet())) {
            Edge sourceEdge = armiesOnEdges.get(army);

            if (!isArmyValidOnEdge(army, sourceEdge)) {
                continue;
            }

            if (isDifferentTeam(sourceEdge.getArmies())) {
                startEdgeBattle(sourceEdge);
            }

            moveArmyFromEdgeToNode(army, sourceEdge);
            maybeCreateRandomEvent();
        }
    }

    /**
     * Returns whether an army is still valid on a given edge, i.e. the edge is
     * non-null and still contains the army.
     *
     * @param army       The army to validate.
     * @param sourceEdge The edge to check.
     * @return {@code true} if the army is present on the edge; {@code false} otherwise.
     */
    private boolean isArmyValidOnEdge(Army army, Edge sourceEdge) {
        return sourceEdge != null && sourceEdge.getArmies().contains(army);
    }

    /**
     * Moves an army from an edge to a randomly chosen endpoint node ({@code from} or {@code to}),
     * then resolves any resulting battle on that node.
     *
     * @param army       The army to move.
     * @param sourceEdge The edge the army is departing from.
     */
    private void moveArmyFromEdgeToNode(Army army, Edge sourceEdge) {
        Node targetNode = ThreadLocalRandom.current().nextBoolean()
                ? sourceEdge.getFrom()
                : sourceEdge.getTo();

        sourceEdge.removeArmy(army);
        targetNode.addArmy(army);

        if (isDifferentTeam(targetNode.getArmies())) {
            startNodeBattle(targetNode);
        }
    }

    /**
     * Rolls for a random event and triggers {@link #createRandomEvent()} if the
     * result falls within {@link #EVENT_CHANCE_PERCENT}.
     */
    private void maybeCreateRandomEvent() {
        if (ThreadLocalRandom.current().nextInt(100) < EVENT_CHANCE_PERCENT) {
            createRandomEvent();
        }
    }

    /**
     * Builds a mapping of each node to its neighboring edges.
     * @param edges The list of edges in the graph.
     * @return A map where the keys are nodes and the values are lists of edges.
     */
    private Map<Node, List<Edge>> buildNeighboringEdges(List<Edge> edges) {
        Map<Node, List<Edge>> neighboringEdges = new HashMap<>();
        for (Edge edge : edges) {
            neighboringEdges.computeIfAbsent(edge.getFrom(), key -> new ArrayList<>()).add(edge);
            if (edge.getTo() != edge.getFrom()) {
                neighboringEdges.computeIfAbsent(edge.getTo(), key -> new ArrayList<>()).add(edge);
            }
        }
        return neighboringEdges;
    }

    /**
     * Checks if there are armies from different teams on a list of armies.
     * @param armies The list of armies to check
     * @return true if there are armies from different teams, false otherwise
     */
    private boolean isDifferentTeam(ArrayList<Army> armies ) {
        for (int i = 0; i < armies.size(); i++) {
            for (int j = i + 1; j < armies.size(); j++) {
                if (!armies.get(i).getTeam().equals(armies.get(j).getTeam())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Starts a battle at the specified node.
     * @param node The node where the battle occurs
     */
    private void startNodeBattle(Node node) {
        System.out.println("Node battle on node " + node.getId());

        // TODO: initiate the Battle class 
    }

    /**
     * Starts a battle at the specified edge.
     * @param edge The edge where the battle occurs
     */
    private void startEdgeBattle(Edge edge) {
        System.out.println("Edge battle on edge " + edge.getId());

        // TODO: initiate the Battle class 
    }

    /**
     * Creates a random event that affects the simulation.
     */
    private void createRandomEvent() {
        System.out.println("A random event has occurred!");

        // TODO: intiate the Event class
    }
    
    /**
     * Ends the simulation and resets to the initial graph state.
     * @param graph The graph to reset.x
     */
    public void endSimulation(Graph graph) {
        timeStep = 0;
        graph = initialGraph; 
    }
}
