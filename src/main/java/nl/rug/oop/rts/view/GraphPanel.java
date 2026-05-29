package nl.view;

import javax.swing.JPanel;
import java.awt.Color;
import nl.rug.oop.rts.model.*;

/**
 * Panel where the graph will be drawn.
 */
public class GraphPanel extends JPanel {

    private static final int nodeSize = 40;
    private final Graph graph;

    /**
     * Creates the graph panel with a temporary background color.
     */
    public GraphPanel() {
        setBackground(Color.RED);
    }



}
