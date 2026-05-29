package nl.rug.oop.rts.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import nl.rug.oop.rts.model.Edge;
import nl.rug.oop.rts.model.Graph;
import nl.rug.oop.rts.model.GraphEventType;
import nl.rug.oop.rts.model.Node;
import nl.rug.oop.rts.util.TextureLoader;

/**
 * Panel where the graph will be drawn.
 */
public class GraphPanel extends JPanel {
    /**
     * Size in pixels of the square used to draw each node.
     */
    private static final int NODE_SIZE = 40;

    /**
     * The graph model this panel observes and draws.
     */
    private final Graph graph;

    /**
     * Background image for the graph panel.
     */
    private final Image backgroundImage;

    /**
     * Horizontal pan offset in screen pixels.
     */
    private int panX;

    /**
     * Vertical pan offset in screen pixels.
     */
    private int panY;

    /**
     * Current zoom level applied to the graph view.
     */
    private double zoom = 1.0;

    /**
     * Minimum allowed zoom level.
     */
    private static final double MIN_ZOOM = 0.5;

    /**
     * Maximum allowed zoom level.
     */
    private static final double MAX_ZOOM = 3.0;

    /**
     * Mouse listener for tracking selected node.
     */
    private GraphMouseListener mouseListener;

    /**
     * Creates the graph panel with a temporary background color and subscribes
     * to graph events so the panel repaints automatically when the graph changes.
     *
     * @param graph The graph model this panel will observe and draw.
     * @param optionsPanel The options panel used for displaying element details.
     */
    public GraphPanel(Graph graph,OptionsPanel optionsPanel) {
        this.graph = graph;

        backgroundImage = TextureLoader.getInstance()
                        .getTexture("mapTexture", 800, 600);

        graph.addListener(GraphEventType.NODE_ADDED, data -> repaint());
        graph.addListener(GraphEventType.NODE_DELETED, data -> repaint());
        graph.addListener(GraphEventType.EDGE_ADDED, data -> repaint());
        graph.addListener(GraphEventType.EDGE_DELETED, data -> repaint());

        this.mouseListener = new GraphMouseListener(graph, this, optionsPanel);
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        addMouseWheelListener(mouseListener);
    }

    /**
     * Paints the panel by drawing all edges and nodes of the graph.
     *
     * @param g Graphics context provided by Swing used to draw on this panel.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // copies the graphics context so we can apply transformations without affecting the original
        Graphics2D g2 = (Graphics2D) g.create();

        try {
            g2.translate(panX, panY);
            g2.scale(zoom, zoom);

            g2.drawImage(backgroundImage, 0, 0, this);

            drawEdges(g2);
            drawNodes(g2);
        } finally {
            // Dispose the graphics context to free up resources.
            g2.dispose();
        }
    }

    /**
     * Draws all edges of the graph as straight lines between their endpoint nodes.
     *
     * @param g Graphics context used to draw the edges.
     */
    private void drawEdges(Graphics g) {
        g.setColor(Color.LIGHT_GRAY);
        for (Edge edge : graph.getEdges()) {
            Node a = edge.getFrom();
            Node b = edge.getTo();
            g.drawLine(a.getX(), a.getY(), b.getX(), b.getY());
        }
    }

    /**
     * Draws all nodes of the graph as orange squares with their name labels.
     *
     * @param g Graphics context used to draw the nodes.
     */
    private void drawNodes(Graphics g) {
        for (Node node : graph.getNodes()) {
            int x = node.getX() - NODE_SIZE / 2;
            int y = node.getY() - NODE_SIZE / 2;

            if (node == mouseListener.getSelectedNode()) {
                g.setColor(Color.RED);
                g.fillRect(x - 5, y - 5, NODE_SIZE + 10, NODE_SIZE + 10);
            }
            g.setColor(Color.ORANGE);
            g.fillRect(x, y, NODE_SIZE, NODE_SIZE);

            g.setColor(Color.WHITE);
            g.drawString(node.getName(), node.getX() - 15, node.getY() + 5);
        }
    }

    /**
     * Clamps a double value between a minimum and maximum.
     * @param value the value to clamp
     * @param min lower bound
     * @param max upper bound
     * @return clamped value
     */
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Returns the background width in world coordinates.
     * @return world width
     */
    public int getWorldWidth() {
        return backgroundImage.getWidth(this);
    }

    /**
     * Returns the background height in world coordinates.
     * @return world height
     */
    public int getWorldHeight() {
        return backgroundImage.getHeight(this);
    }

    /**
     * Moves the visible map by the supplied delta.
     * @param deltaX horizontal delta in pixels
     * @param deltaY vertical delta in pixels
     */
    public void panBy(int deltaX, int deltaY) {
        this.panX += deltaX;
        this.panY += deltaY;

        repaint();
    }

    /**
     * Converts a screen X coordinate to a world X coordinate.
     * @param screenX the x coordinate in panel space
     * @return the x coordinate in graph space
     */
    public double toWorldX(int screenX) {
        return (screenX - panX) / zoom;
    }

    /**
     * Converts a screen Y coordinate to a world Y coordinate.
     * @param screenY the y coordinate in panel space
     * @return the y coordinate in graph space
     */
    public double toWorldY(int screenY) {
        return (screenY - panY) / zoom;
    }

    /**
     * Zooms the view by the given factor while keeping the supplied screen point anchored.
     * @param factor zoom multiplier
     * @param anchorX screen x coordinate to keep stable
     * @param anchorY screen y coordinate to keep stable
     */
    public void zoomBy(double factor, int anchorX, int anchorY) {
        double newZoom = clamp(zoom * factor, MIN_ZOOM, MAX_ZOOM);
        double worldX = toWorldX(anchorX);

        System.out.println("World coordinates before zoom: (" + worldX + ", " + toWorldY(anchorY) + ")");
        double worldY = toWorldY(anchorY);

        zoom = newZoom;
        panX = (int) Math.round(anchorX - worldX * zoom);
        panY = (int) Math.round(anchorY - worldY * zoom);

        repaint();
    }

    /**
     * Returns the current zoom level.
     * @return current zoom factor
     */
    public double getZoom() {
        return zoom;
    }

    /**
     * Returns the current horizontal pan offset.
     * @return horizontal offset
     */
    public int getPanX() {
        return panX;
    }

    /**
     * Returns the current vertical pan offset.
     * @return vertical offset
     */
    public int getPanY() {
        return panY;
    }
}