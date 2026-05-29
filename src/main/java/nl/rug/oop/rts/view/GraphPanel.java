package nl.rug.oop.rts.view;

import javax.swing.JPanel;

import java.awt.Color;

import nl.rug.oop.rts.model.Edge;
import nl.rug.oop.rts.model.Graph;
import nl.rug.oop.rts.model.GraphEventType;
import nl.rug.oop.rts.util.TextureLoader;

import java.awt.Graphics;
import java.awt.Image;

import nl.rug.oop.rts.model.Node;

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
     * Creates the graph panel with a temporary background color and subscribes
     * to graph events so the panel repaints automatically when the graph changes.
     *
     * @param graph The graph model this panel will observe and draw.
     */
    public GraphPanel(Graph graph) {
        this.graph = graph;
        
        backgroundImage = TextureLoader.getInstance()
                        .getTexture("mapTexture", 800, 600);

        graph.addListener(GraphEventType.NODE_ADDED, data -> repaint());
        graph.addListener(GraphEventType.NODE_DELETED, data -> repaint());
        graph.addListener(GraphEventType.EDGE_ADDED, data -> repaint());
        graph.addListener(GraphEventType.EDGE_DELETED, data -> repaint());

        GraphMouseListener mouseListener = new GraphMouseListener(graph);
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
    }

    /**
     * Paints the panel by drawing all edges and nodes of the graph.
     *
     * @param g Graphics context provided by Swing used to draw on this panel.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        drawEdges(g);
        drawNodes(g);
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

            g.setColor(Color.ORANGE);
            g.fillRect(x, y, NODE_SIZE, NODE_SIZE);

            g.setColor(Color.WHITE);
            g.drawString(node.getName(), node.getX() - 15, node.getY() + 5);
        }
    }
}