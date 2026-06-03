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
    private Graph initialGraph;

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
        initialGraph = copyGraph(graph);
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

        if(timeStep == 0) {
            System.out.println("Already at initial state, cannot revert further.");
            return;
        }

        Graph previousState = history.pop();
        restoreGraphState(graph, previousState);
        timeStep--;
        System.out.println("Reverted simulation to time step " + timeStep);
    }

    /**
     * Advances the simulation by one time step, moving armies and resolving battles and events.
     *
     * @param graph The graph to advance.
     */
    public void advanceTime(Graph graph) {
        history.push(copyGraph(graph));

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
                startBattle(source.getArmies(), source);
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

        source.getArmies().remove(army);
        selectedEdge.getArmies().add(army);

        if (isDifferentTeam(selectedEdge.getArmies())) {
            startBattle(selectedEdge.getArmies(), selectedEdge);
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
                startBattle(sourceEdge.getArmies(), sourceEdge);
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

        sourceEdge.getArmies().remove(army);
        targetNode.getArmies().add(army);

        if (isDifferentTeam(targetNode.getArmies())) {
            startBattle(targetNode.getArmies(), targetNode);
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
     * Creates a deep copy of the graph structure and army placement.
     *
     * @param source the graph to copy
     * @return a detached copy of the supplied graph
     */
    private Graph copyGraph(Graph source) {
        Graph copy = new Graph();

        Map<Integer, Node> nodeCopiesById = copyNodes(source, copy);
        copyEdges(source, copy, nodeCopiesById);
        copyGraphMetadata(source, copy, nodeCopiesById);

        return copy;
    }

    /**
     * Deep-copies all nodes from {@code source} into {@code copy}, including their armies.
     *
     * @param source the graph to copy nodes from
     * @param copy   the graph to copy nodes into
     * @return a map of original node IDs to their corresponding copied {@link Node}s,
     *         used to resolve edge endpoints and selection state
     */
    private Map<Integer, Node> copyNodes(Graph source, Graph copy) {
        Map<Integer, Node> nodeCopiesById = new HashMap<>();

        for (Node originalNode : source.getNodes()) {
            Node nodeCopy = new Node(
                    originalNode.getId(),
                    originalNode.getName(),
                    originalNode.getX(),
                    originalNode.getY());

            for (Army originalArmy : originalNode.getArmies()) {
                nodeCopy.getArmies().add(copyArmy(originalArmy));
            }

            copy.getNodes().add(nodeCopy);
            nodeCopiesById.put(originalNode.getId(), nodeCopy);
        }

        return nodeCopiesById;
    }

    /**
     * Deep-copies all edges from {@code source} into {@code copy}, including their armies.
     * Endpoint nodes are resolved via {@code nodeCopiesById}.
     *
     * @param source         the graph to copy edges from
     * @param copy           the graph to copy edges into
     * @param nodeCopiesById a map of original node IDs to copied {@link Node}s,
     *                       used to wire up edge endpoints
     */
    private void copyEdges(Graph source, Graph copy, Map<Integer, Node> nodeCopiesById) {
        for (Edge originalEdge : source.getEdges()) {
            Edge edgeCopy = new Edge(
                    originalEdge.getId(),
                    originalEdge.getName(),
                    nodeCopiesById.get(originalEdge.getFrom().getId()),
                    nodeCopiesById.get(originalEdge.getTo().getId()));

            for (Army originalArmy : originalEdge.getArmies()) {
                edgeCopy.getArmies().add(copyArmy(originalArmy));
            }

            copy.getEdges().add(edgeCopy);
        }
    }

    /**
     * Copies ID counters and selection state from {@code source} onto {@code copy}.
     *
     * @param source         the graph to read metadata from
     * @param copy           the graph to apply metadata to
     * @param nodeCopiesById a map of original node IDs to copied {@link Node}s,
     *                       used to restore the selected node reference
     */
    private void copyGraphMetadata(Graph source, Graph copy, Map<Integer, Node> nodeCopiesById) {
        copy.setNextNodeId(source.getNextNodeId());
        copy.setNextEdgeId(source.getNextEdgeId());

        copySelectedNode(source, copy, nodeCopiesById);
        copySelectedEdge(source, copy);
    }

    /**
     * Restores the selected node on {@code copy} to the equivalent copied node,
     * if one was selected in {@code source}.
     *
     * @param source         the graph to read the selected node from
     * @param copy           the graph to apply the selected node to
     * @param nodeCopiesById a map of original node IDs to copied {@link Node}s
     */
    private void copySelectedNode(Graph source, Graph copy, Map<Integer, Node> nodeCopiesById) {
        if (source.getSelectedNode() == null) {
            return;
        }
        copy.setSelectedNode(nodeCopiesById.get(source.getSelectedNode().getId()));
    }

    /**
     * Restores the selected edge on {@code copy} to the equivalent copied edge,
     * if one was selected in {@code source}.
     *
     * @param source the graph to read the selected edge from
     * @param copy   the graph to apply the selected edge to
     */
    private void copySelectedEdge(Graph source, Graph copy) {
        if (source.getSelectedEdge() == null){
            return;
        }

        for (Edge edge : copy.getEdges()) {
            if (edge.getId() == source.getSelectedEdge().getId()) {
                copy.setSelectedEdge(edge);
                break;
            }
        }
    }

    /**
     * Creates a deep copy of an Army object.
     *
     * @param original the army to copy
     * @return a new Army with the same faction and a copy of the units list
     */
    private Army copyArmy(Army original) {
        ArrayList<Unit> unitCopies = new ArrayList<>();

        for (Unit originalUnit : original.getUnits()) {
            unitCopies.add(copyUnit(originalUnit));
        }

        return new Army(original.getFaction(), unitCopies);
    }

    /**
     * Creates a deep copy of a Unit object, including its history.
     * @param original the unit to copy
     * @return a new Unit with the same properties
     */
    public Unit copyUnit(Unit original) {
        Unit u = new Unit(original.getName(),
                original.getHealth(), original.getDamage(),
                original.getAbility(), new ArrayList<>());
        return u;
    }

    /**
     * Restores a live graph to the provided snapshot.
     *
     * @param target the graph to update
     * @param snapshot the snapshot to apply
     */
    private void restoreGraphState(Graph target, Graph snapshot) {
        Graph copiedSnapshot = copyGraph(snapshot);
        target.setNodes(copiedSnapshot.getNodes());
        target.setEdges(copiedSnapshot.getEdges());
        target.setNextNodeId(copiedSnapshot.getNextNodeId());
        target.setNextEdgeId(copiedSnapshot.getNextEdgeId());
        target.setSelectedNode(copiedSnapshot.getSelectedNode());
        target.setSelectedEdge(copiedSnapshot.getSelectedEdge());
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
     * Starts a battle between the given armies at the specified location and returns the result.
     * @param armies the armies participating in the battle
     * @param location the location where the battle takes place
     * @return the result of the battle, including the winner and damage/kill attribution
     */
    private BattleResult startBattle(ArrayList<Army> armies, BattleLocation location) {
        Battle battle = new Battle(armies); 

        BattleResult result = battle.resolve();

        System.out.println("Battle at " + location.getName() + " resolved. Winning team: " + result.getWinner());

        return result;
    }

    /**
     * Creates a random event that affects the simulation.
     */
    private void createRandomEvent() {
        System.out.println("A random event has occurred!");

        // TODO: intiate the Event class
    }
       
    /**
     * Ends the simulation and restores all armies to their initial positions.
     * @param graph The graph to reset.
     */
    public void startSimulation(Graph graph) {
        initialGraph = copyGraph(graph);
    }

    /**
     * Ends the simulation and restores all armies to their initial positions.
     * @param graph The graph to reset.
     */
    public void endSimulation(Graph graph) {
        history.clear();
        timeStep = 0;
        restoreInitialArmies(graph);
    }

    /**
     * Restores all armies to their initial positions based on the initial graph snapshot.
     *
     * @param graph the graph whose armies should be restored
     */
    private void restoreInitialArmies(Graph graph) {
        for (Node node : graph.getNodes()) {
            node.getArmies().clear();
        }
        for (Edge edge : graph.getEdges()) {
            edge.getArmies().clear();
        }
      
        for (Node initialNode : initialGraph.getNodes()) {
            Node currentNode = graph.getNode(initialNode.getId());
            System.out.println("Restoring armies on node " + currentNode.getName());
            for (Army initialArmy : initialNode.getArmies()) {
                currentNode.getArmies().add(copyArmy(initialArmy));
            }
        }

        for (Edge initialEdge : initialGraph.getEdges()) {
            Edge currentEdge = graph.getEdge(initialEdge.getId());
            for (Army initialArmy : initialEdge.getArmies()) {
                currentEdge.getArmies().add(copyArmy(initialArmy));
            }
        }
    }
}
