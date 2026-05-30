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
    private static final int NODE_SIZE = 40;

    /**
     * Constructor for the graph mouse listener.
     * @param graph Graph model that this listener will interact with. Must not be null.
     * @param graphPanel Graph panel that will be repainted and panned.
     * @param optionsPanel Options panel for displaying element details.
     */
    
    public GraphMouseListener(Graph graph, GraphPanel graphPanel) {
        this.graph = graph;
        this.graphPanel = graphPanel;
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

        Node clickedNode = findNodeAt(worldX, worldY);
        if (clickedNode != null) {
            if (edgeStartNode != null && edgeStartNode != clickedNode) {
                int id = graph.nextEdgeId();
                String name = "Edge " + id;
                Edge newEdge = new Edge(id, name, edgeStartNode, clickedNode);
                graph.addEdge(newEdge);
                edgeStartNode = null;
            } else {
                graph.setSelectedNode(clickedNode);
            }
            return;
        }

        Edge clickedEdge = findEdgeAt(worldX, worldY);
        if (clickedEdge != null) {
            graph.setSelectedEdge(clickedEdge);
            return;
        }

        edgeStartNode = null;
        graph.clearSelection();
        panning = true;
    }

    private Node findNodeAt(double worldX, double worldY) {
        for (Node node : graph.getNodes()) {
            double dx = Math.abs(worldX-node.getX());
            double dy = Math.abs(worldY-node.getY());
            if (dx<=NODE_SIZE&&dy<=NODE_SIZE) {
                return node;
            }
        }
        return null;
    }

    private Edge findEdgeAt(double worldX, double worldY) {
        final double EDGE_CLICK_THRESHOLD = 10.0; 
        for (Edge edge : graph.getEdges()) {
            Node n1 = edge.getFrom();
            Node n2 = edge.getTo();
            double distance = distancePointToLine(worldX, worldY, n1.getX(), n1.getY(), n2.getX(), n2.getY());
            if (distance <= EDGE_CLICK_THRESHOLD) {
                return edge;
            }
        }
        return null;
    }

    private double distancePointToPoint(double px, double py, double x, double y) {
        double dx = px - x;
        double dy = py - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private double distancePointToLine(double px, double py, double x1, double y1, double x2, double y2) {
        // vector AB from (x1, y1) to (x2, y2)
        double lx = x2 - x1;
        double ly = y2 - y1;
        // vector AP from (x1, y1) to (px, py)
        double dx = px - x1;
        double dy = py - y1;

        // project AP onto AB to find the closest point on the line
        double dotProduct = dx * lx + dy * ly;
        double lineLenSquared = lx * lx + ly * ly; 
        double ratio = dotProduct / lineLenSquared; 

        if (ratio < 0) {
            return 1000;
        } else if (ratio > 1) {
            return 1000;
        } else {
            double closestX = x1 + ratio * lx;
            double closestY = y1 + ratio * ly;
            return distancePointToPoint(px, py, closestX, closestY);
        }
    }

    public void StartAddingEdge(Node startNode) {
        this.edgeStartNode = startNode; 
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

        int r = NODE_SIZE / 2;

        int minX = r;
        int minY = r;
        int maxX = graphPanel.getWorldWidth() - r;
        int maxY = graphPanel.getWorldHeight() - r;

        Node sel = graph.getSelectedNode();
        if (sel != null) {
            int newX = (int) Math.round(graphPanel.toWorldX(x));
            int newY = (int) Math.round(graphPanel.toWorldY(y));

            sel.setX(clamp(newX, minX, maxX));
            sel.setY(clamp(newY, minY, maxY));

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
