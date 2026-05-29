package nl.rug.oop.rts.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import lombok.Getter;
import nl.rug.oop.rts.model.Graph;
import nl.rug.oop.rts.model.Node;

/**
 * Mouse listener for handling mouse events on the graph panel.
 */
public class GraphMouseListener extends MouseAdapter {
    /**
     * Reference to the graph model. This allows the listener to access the graph's
     */
    private final Graph graph;

    /**
     * Radius for detecting clicks on nodes.
     */
    @Getter
    private Node selectedNode = null;

    /**
     * Radius for detecting clicks on nodes. Add more details here.
     * @param graph Graph model that this listener will interact with. Must not be null.
     */ 
    private static final int NODE_RADIUS = 40;

    /**
     * Constructor for the graph mouse listener.
     * @param graph Graph model that this listener will interact with. Must not be null.
     */
    public GraphMouseListener(Graph graph) {
        this.graph = graph;
    }

    /**
     * Handle mouse pressed events.
     * @param e MouseEvent containing details about the mouse pressed event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        for (Node n : graph.getNodes()) {
            int dx = x - n.getX();
            int dy = y - n.getY();

            if (dx * dx + dy * dy <= NODE_RADIUS * NODE_RADIUS) {
                selectedNode = n;
                break;
            }
        }

    }

    /**
     * Handle mouse dragged events.
     * @param e MouseEvent containing details about the mouse dragged event.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        if (selectedNode == null) {
            return;
        }

        selectedNode.setX(e.getX());
        selectedNode.setY(e.getY());
        e.getComponent().repaint();
    }

    /**
     * Handle mouse released events.
     * @param e MouseEvent containing details about the mouse released event.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        selectedNode = null;
        e.getComponent().repaint();
    }
    

    /**
     * Handle mouse clicked events.
     * @param e MouseEvent containing details about the mouse clicked event.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        System.out.println("Mouse clicked at: (" + x + ", " + y + ")");

        for (Node n : graph.getNodes()) {
            int dx = x - n.getX();
            int dy = y - n.getY();

            if (dx * dx + dy * dy <= NODE_RADIUS * NODE_RADIUS) {
                // TODO: Emit a node clicked event to the graph model
                // TODO: highlight the clicked node in the UI
                System.out.println("Node clicked: " + n.getName());
                selectedNode = n;

                break;
            } else {
                // TODO: Handle case when no node is clicked
                System.out.println("Map is clicked, so remove node selection");
                selectedNode = null;
            }
        }
    }
}       
