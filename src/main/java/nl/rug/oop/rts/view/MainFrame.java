package nl.rug.oop.rts.view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;

import nl.rug.oop.rts.model.*;
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
        configureWindow();
        Graph graph = new Graph(); 
        setJMenuBar(createMenuBar());
        add(createSplitPane(graph));
    }

    /**
     * Applies the base window configuration.
     */
    private void configureWindow() {
        setTitle("Lord of the Rings Game");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }


    /**
     * Builds the top menu bar with graph action buttons.
     * @return the configured menu bar
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JButton addNodeButton = new JButton("Add Node");
        JButton addEdgeButton = new JButton("Add Edge");
        JButton removeNodeButton = new JButton("Remove Node");
        JButton removeEdgeButton = new JButton("Remove Edge");

        menuBar.add(addNodeButton);
        menuBar.add(addEdgeButton);
        menuBar.add(removeNodeButton);
        menuBar.add(removeEdgeButton);
        return menuBar;
    }

    /**
     * Creates the split view that combines the graph panel and the options panel.
     * @param graph graph used by the panels
     * @return a split pane with the graph view on the left and the options panel on the right
     */
    private JSplitPane createSplitPane(Graph graph) {
        OptionsPanel optionsPanel = new OptionsPanel();
        GraphPanel graphPanel = new GraphPanel(graph);
     
        optionsPanel.setOnNameChanged(graphPanel::repaint);
      
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, optionsPanel, graphPanel);

        // Give the graph panel the majority of the horizontal space
        splitPane.setDividerLocation(220);

        return splitPane;
    }
}
