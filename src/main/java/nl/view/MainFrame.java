package nl.view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

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

        GraphPanel graphPanel = new GraphPanel();
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
