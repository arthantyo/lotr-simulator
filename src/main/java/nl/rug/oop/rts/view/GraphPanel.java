package nl.rug.oop.rts.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.BasicStroke;
import javax.swing.JPanel;
import lombok.Getter;

import nl.rug.oop.rts.model.Edge;
import nl.rug.oop.rts.model.Graph;
import nl.rug.oop.rts.model.GraphEventType;
import nl.rug.oop.rts.model.Node;
import nl.rug.oop.rts.util.TextureLoader;
import nl.rug.oop.rts.controller.*;

/**
 * Panel where the graph will be drawn.
 */
public class GraphPanel extends JPanel {
    /**
     * Size in pixels of the square used to draw each node.
     */
    private static final int NODE_SIZE = 60;

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
     * Mouse listener for tracking selected node.
     */
    @Getter
    private GraphMouseListener mouseListener;

    /**
     * Stroke used to draw edges.
     */
    private final BasicStroke defaultStroke = new BasicStroke(
        2.0f,                   
        BasicStroke.CAP_ROUND,  
        BasicStroke.JOIN_ROUND,  
        0,
        new float[]{10.0f, 6.0f},
        0
    );

    /**
     * Creates the graph panel with a temporary background color and subscribes
     * to graph events so the panel repaints automatically when the graph changes.
     *
     * @param graph The graph model this panel will observe and draw.
     * @param commandManager Manager that records reversible actions for undo/redo.
     */
    public GraphPanel(Graph graph, CommandManager commandManager) {
        this.graph = graph;

        backgroundImage = TextureLoader.getInstance()
                        .getTexture("mapTexture", 800, 600);

        graph.addListener(GraphEventType.NODE_ADDED, data -> repaint());
        graph.addListener(GraphEventType.NODE_DELETED, data -> repaint());
        graph.addListener(GraphEventType.EDGE_ADDED, data -> repaint());
        graph.addListener(GraphEventType.EDGE_DELETED, data -> repaint());
        graph.addListener(GraphEventType.SELECTION_CHANGED, data -> repaint());

        this.mouseListener = new GraphMouseListener(graph, this, commandManager);
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
            // Draw background to fill entire panel (no transformations)
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

            // Apply transformations for graph elements only
            Graphics2D g2Transformed = (Graphics2D) g2.create();
            g2Transformed.translate(panX, panY);
            g2Transformed.scale(zoom, zoom);

            drawEdges(g2Transformed);
            drawNodes(g2Transformed);

            g2Transformed.dispose();
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
        Graphics2D g2 = (Graphics2D) g;
        for (Edge edge : graph.getEdges()) {
            Node a = edge.getFrom();
            Node b = edge.getTo();
            if (edge == graph.getSelectedEdge()) {
                g2.setColor(Color.RED);
                g2.setStroke(defaultStroke);
            } else {
                g2.setColor(Color.BLUE);
                g2.setStroke(defaultStroke);
            }
            g.drawLine(a.getX(), a.getY(), b.getX(), b.getY());
        }
    }

    /**
     * Draws all nodes in the graph.
     *
     * @param g Graphics context used for rendering.
     */
    private void drawNodes(Graphics g) {
        for (Node node : graph.getNodes()) {
            drawNode(g, node);
        }
    }

    /**
     * Draws a single node, including its selection highlight,
     * image, and name label.
     *
     * @param g Graphics context used for rendering.
     * @param node The node to draw.
     */
    private void drawNode(Graphics g, Node node) {
        int x = node.getX() - NODE_SIZE / 2;
        int y = node.getY() - NODE_SIZE / 2;

        if (node == graph.getSelectedNode()) {
            drawSelectionHighlight(g, x, y);
        }

        drawNodeImage(g, node, x, y);
        drawNodeLabel(g, node);
    }

    /**
     * Draws a rounded red highlight behind the selected node.
     *
     * @param g Graphics context used for rendering.
     * @param x Top-left x-coordinate of the node.
     * @param y Top-left y-coordinate of the node.
     */
    private void drawSelectionHighlight(Graphics g, int x, int y) {
        int padding = 12;

        g.setColor(Color.RED);
        g.fillRoundRect(
            x - padding,
            y - padding - 6,
            NODE_SIZE + padding * 2,
            NODE_SIZE + padding * 3,
            12,
            12
        );
    }

    /**
     * Draws the node texture centered on the node's position.
     *
     * @param g Graphics context used for rendering.
     * @param node The node whose texture is drawn.
     * @param x Top-left x-coordinate of the node.
     * @param y Top-left y-coordinate of the node.
     */
    private void drawNodeImage(Graphics g, Node node, int x, int y) {
        int imageSize = NODE_SIZE * 2;

        Image nodeImage = TextureLoader.getInstance()
                .getTexture("node2", x, y);

        g.drawImage(
            nodeImage,
            node.getX() - imageSize / 2,
            node.getY() - imageSize / 2,
            imageSize,
            imageSize,
            this
        );
    }

    /**
     * Draws the node's name centered on the node.
     *
     * @param g Graphics context used for rendering.
     * @param node The node whose label is drawn.
     */
    private void drawNodeLabel(Graphics g, Node node) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));

        FontMetrics fm = g.getFontMetrics();
        String name = node.getName();

        int textWidth = fm.stringWidth(name);

        g.drawString(
            name,
            node.getX() - textWidth / 2,
            node.getY() + fm.getAscent() / 2
        );
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
        double newZoom = zoom * factor;
        double worldX = toWorldX(anchorX);
        double worldY = toWorldY(anchorY);

        // System.out.println("World coordinates before zoom: (" + worldX + ", " + toWorldY(anchorY) + ")");

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
