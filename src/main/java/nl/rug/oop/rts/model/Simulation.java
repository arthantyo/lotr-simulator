package nl.rug.oop.rts.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import lombok.Getter;

public class Simulation {

    /*
     * The initial graph state
     */
    private final Graph initialGraph;

    /**
     * Current time step of the simulation.
     */
    @Getter
    private int timeStep = 0; 

    /**
     * Event chance
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
     * Reverts the simulation to the previous time step
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
     * @param graph The graph to advance. Must not be null.
     */
    public void advanceTime(Graph graph) {
        history.push(graph);

        System.out.println("Advancing simulation to time step " + (timeStep + 1));
        timeStep++;

        // Snapshot all armies currently on nodes and edges (so moves don't interfere)
        Map<Army, Node> armiesOnNodes = new HashMap<>();
        for (Node node : graph.getNodes()) {
            for (Army army : node.getArmies()) {
                armiesOnNodes.put(army, node);
            }
        }

        Map<Army, Edge> armiesOnEdges = new HashMap<>();
        for (Edge edge : graph.getEdges()) {
            for (Army army : edge.getArmies()) {
                armiesOnEdges.put(army, edge);
            }
        }

        Map<Node, List<Edge>> neighboringEdges = buildNeighboringEdges(graph.getEdges());

        /*
        * For each node with an army, randomly decide 
        * to move it to one of its neighboring edges.
        */
        for (Army army : new ArrayList<>(armiesOnNodes.keySet())) {
            Node source = armiesOnNodes.get(army);
            if (source == null) continue;
             
            if (!source.getArmies().contains(army)) continue;

            // resolve battles on the node before moving
            if (isDifferentTeam(source.getArmies())) {
                startNodeBattle(source);
            }

            if (neighborEdges == null || neighborEdges.isEmpty()) continue;

            int randomEdgeIndex = ThreadLocalRandom.current().nextInt(neighborEdges.size());
            Edge selectedEdge = neighborEdges.get(randomEdgeIndex);

            source.removeArmy(army);
            selectedEdge.addArmy(army);

           
            if (isDifferentTeam(selectedEdge.getArmies())) {
                startEdgeBattle(selectedEdge);
            }


            // TODO: add event chance
            int eventChance = ThreadLocalRandom.current().nextInt(100);

            if (eventChance < EVENT_CHANCE_PERCENT) {
                // TODO: implement event logic
                createRandomEvent();
            }
        }

        /*
        * For each edge with an army, randomly decide to move it 
        * to either the `from` or `to` node.
        */
        for (Army army : new ArrayList<>(armiesOnEdges.keySet())) {
            Edge sourceEdge = armiesOnEdges.get(army);
            if (sourceEdge == null|| !sourceEdge.getArmies().contains(army)){
                continue;
            };

            // resolve battles on the edge before moving
            if (isDifferentTeam(sourceEdge.getArmies())) {
                startEdgeBattle(sourceEdge);
            }

            Node targetNode = ThreadLocalRandom.current().nextBoolean() ? sourceEdge.getFrom() : sourceEdge.getTo();

            sourceEdge.removeArmy(army);
            targetNode.addArmy(army);
           
            if (isDifferentTeam(targetNode.getArmies())) {
                startNodeBattle(targetNode);
            } 

            // TODO: add event chance
            int eventChance = ThreadLocalRandom.current().nextInt(100);

            if (eventChance < EVENT_CHANCE_PERCENT) {
                // TODO: implement event logic
                createRandomEvent();
            }
        }
    }

    /**
     * Builds a mapping of each node to its neighboring edges
     * @param edges The list of edges in the graph
     * @return A map where the keys are nodes and the values are lists of edges 
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
     * Checks if there are armies from different teams on a list of armies
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
     * Starts a battle at the specified node
     * @param node The node where the battle occurs
     */
    private void startNodeBattle(Node node) {
        System.out.println("Node battle on node " + node.getId());

        // TODO: initiate the Battle class 
    }

    /**
     * Starts a battle at the specified edge
     * @param edge The edge where the battle occurs
     */
    private void startEdgeBattle(Edge edge) {
        System.out.println("Edge battle on edge " + edge.getId());

        // TODO: initiate the Battle class 
    }

    /**
     * Creates a random event that affects the simulation
     */
    private void createRandomEvent() {
        System.out.println("A random event has occurred!");

        // TODO: intiate the Event class
    }
    
    /**
     * Ends the simulation and resets to the initial graph state
     * @param graph The graph to reset. Must not be null.
     */
    public void endSimulation(Graph graph) {
        timeStep = 0;
        graph = initialGraph; 
    }
}
