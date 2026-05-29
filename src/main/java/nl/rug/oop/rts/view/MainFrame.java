package nl.rug.oop.rts.view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import nl.rug.oop.rts.model.*;

/**
 * Main frame of the application. Add more details here.
 */
public class MainFrame extends JFrame {
    /**
     * Creates the main frame of the application. Add more details here.
     */
    public MainFrame() {
        setTitle("Lord of the Rings Game");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

    }
}
