package nl.view;

import javax.swing.JPanel;
import java.awt.Color;

/**
 * Panel where the graph will be drawn. Occupies the left (larger) portion
 * of the main split pane and will be responsible for rendering nodes,
 * edges, and handling user interactions on the graph canvas.
 */
public class GraphPanel extends JPanel {

    /**
     * Creates the graph panel with a temporary red background.
     * The red colour is a placeholder to make the panel visible
     * before actual graph rendering is implemented.
     */
    public GraphPanel() {
        setBackground(Color.RED);
    }
}