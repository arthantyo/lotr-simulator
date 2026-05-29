package nl.rug.oop.rts.view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import nl.rug.oop.rts.model.*;
import javax.swing.JSplitPane;

/**
 * Main frame of the application. Serves as the top-level window,
 * containing a toolbar for graph manipulation actions and a horizontally
 * split view between the graph canvas and the options panel.
 */
public class MainFrame extends JFrame {

    /**
     * Constructs and configures the main application window.
     * Initialises the menu bar with graph editing buttons, creates the
     * graph and options panels, and arranges them in a split pane.
     */
    public MainFrame() {
        setTitle("Lord of the Rings Game");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Centre the window on the screen
        setLocationRelativeTo(null);

        Graph graph = new Graph();
        
        // === 1.3 test ===
        Node gondor = new Node(0, "Gondor", 250, 300);
        Node mordor = new Node(1, "Mordor", 700, 500);
        Node rohan  = new Node(2, "Rohan",  450, 150);
        graph.addNode(gondor);
        graph.addNode(mordor);
        graph.addNode(rohan);
        graph.addEdge(new Edge(0, "Path1", gondor, mordor));
        graph.addEdge(new Edge(1, "Path2", gondor, rohan));

        GraphPanel graphPanel = new GraphPanel(graph);
        add(graphPanel);

        // Use JMenuBar as a horizontal toolbar at the top of the window
        JMenuBar menuBar = new JMenuBar();

        JButton addNodeButton = new JButton("Add Node");
        JButton addEdgeButton = new JButton("Add Edge");
        JButton removeNodeButton = new JButton("Remove Node");
        JButton removeEdgeButton = new JButton("Remove Edge");

        menuBar.add(addNodeButton);
        menuBar.add(addEdgeButton);
        menuBar.add(removeNodeButton);
        menuBar.add(removeEdgeButton);

        setJMenuBar(menuBar);

        GraphPanel graphPanel1 = new GraphPanel();
        OptionsPanel optionsPanel = new OptionsPanel();

        // Temporary hardcoded nodes and edge to demonstrate the options panel
        Node from = new Node(1, "Node 1");
        Node to = new Node(2, "Node 2");
        Edge edge = new Edge(1, "Edge 1", from, to);
        optionsPanel.showEdgeMenu(edge);

        // Left side gets the graph canvas, right side gets the options panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, graphPanel1, optionsPanel);
        // Give the graph panel the majority of the horizontal space
        splitPane.setDividerLocation(750);
        add(splitPane);
    }
}
