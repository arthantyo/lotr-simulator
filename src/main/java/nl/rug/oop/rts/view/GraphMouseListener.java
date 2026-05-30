package nl.rug.oop.rts.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import lombok.Getter;
import nl.rug.oop.rts.model.*;
/**
 * Mouse listener for handling mouse events on the graph panel.
 */
public class GraphMouseListener extends MouseAdapter {
    /**
     * Reference to the graph model. This allows the listener to access the graph's
     */
    private final Graph graph;

    /**
     * Panel that draws the graph and keeps track of the current pan offset.
     */
    private final GraphPanel graphPanel;

    /**
     * True while the user is dragging the background to pan the map.
     */
    private boolean panning;

    /**
     * Last mouse X position used to compute pan deltas.
     */
    private int lastMouseX;

    /**
     * Last mouse Y position used to compute pan deltas.
     */
    private int lastMouseY;

    private Node edgeStartNode = null;

    /**
    * Radius for detecting clicks on nodes. Add more details here.
    * @param graph Graph model that this listener will interact with. Must not be null.
    * @param graphPanel Graph panel that will be repainted and panned.
    */ 
    private static final int NODE_RADIUS = 40;

    /**
     * Panel for displaying details of the selected graph element.
     */
    private final OptionsPanel optionsPanel;
    /**
     * Constructor for the graph mouse listener.
     * @param graph Graph model that this listener will interact with. Must not be null.
     * @param graphPanel Graph panel that will be repainted and panned.
     * @param optionsPanel Options panel for displaying element details.
     */
    
    public GraphMouseListener(Graph graph, GraphPanel graphPanel, OptionsPanel optionsPanel) {
        this.graph = graph;
        this.graphPanel = graphPanel;
        this.optionsPanel = optionsPanel;
    }

    /**
     * Handle mouse pressed events.
     * @param e MouseEvent containing details about the mouse pressed event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        lastMouseX = x;
        lastMouseY = y;
        panning = false;

        double worldX = graphPanel.toWorldX(x);
        double worldY = graphPanel.toWorldY(y);

        Node clickedNode = null;
        for (Node n : graph.getNodes()) {
            double dx = worldX - n.getX();
            double dy = worldY - n.getY();

            if (dx * dx + dy * dy <= NODE_RADIUS * NODE_RADIUS) {
                clickedNode = n;
                break;
            }
        }

        if (clickedNode != null) {
            selectedNode = clickedNode;
            optionsPanel.showNodeMenu(clickedNode);
        } else {
            selectedNode = null;
            optionsPanel.showNothingSelected();
            panning = true;
        }

        graphPanel.repaint();
    }

    /**
     * Clamps an integer value between a minimum and maximum.
     * @param value the value to clamp
     * @param min lower bound
     * @param max upper bound
     * @return clamped value
     */
    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Handle mouse dragged events.
     * @param e MouseEvent containing details about the mouse dragged event.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        int r = NODE_RADIUS / 2;

        int minX = r;
        int minY = r;
        int maxX = graphPanel.getWorldWidth() - r;
        int maxY = graphPanel.getWorldHeight() - r;

        if (selectedNode != null) {
            int newX = (int) Math.round(graphPanel.toWorldX(x));
            int newY = (int) Math.round(graphPanel.toWorldY(y));

            selectedNode.setX(clamp(newX, minX, maxX));
            selectedNode.setY(clamp(newY, minY, maxY));

            graphPanel.repaint();
            return;
        }

        if (panning) {
            graphPanel.panBy(x - lastMouseX, y - lastMouseY);
            lastMouseX = x;
            lastMouseY = y;
        }
    }

    /**
     * Handle mouse released events.
     * @param e MouseEvent containing details about the mouse released event.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        panning = false;
        graphPanel.repaint();
    }
    

    /**
     * Handle mouse clicked events.
     * @param e MouseEvent containing details about the mouse clicked event.
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        // Selection is now handled in mousePressed
    }

    /**
     * Handle mouse wheel events to zoom the graph in and out.
     * @param e mouse wheel event containing the scroll amount and cursor position
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double notches = e.getPreciseWheelRotation();
        double zoomFactor = Math.pow(1.1, -notches);
        graphPanel.zoomBy(zoomFactor, e.getX(), e.getY());
    }
}       
