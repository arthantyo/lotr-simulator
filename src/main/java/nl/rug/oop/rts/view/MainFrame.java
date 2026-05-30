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
        GraphPanel graphPanel = new GraphPanel(graph);
        OptionsPanel optionsPanel = new OptionsPanel();
        optionsPanel.setOnNameChanged(graphPanel::repaint);

        wireOptionsMenu(graph, optionsPanel);
        setJMenuBar(createMenuBar(graph, graphPanel.getMouseListener()));
        add(createSplitPane(graphPanel, optionsPanel));
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
     * Builds the top menu bar with graph action buttons and wires the
     * selection-dependent buttons to the model so they enable/disable
     * according to the current selection.
     *
     * @param graph         the graph model the buttons operate on
     * @param mouseListener listener used to start edge creation
     * @return the configured menu bar
     */
    private JMenuBar createMenuBar(Graph graph, GraphMouseListener mouseListener) {
        JButton addNode = createAddNodeButton(graph);
        JButton addEdge = createAddEdgeButton(graph, mouseListener);
        JButton removeNode = createRemoveNodeButton(graph);
        JButton removeEdge = createRemoveEdgeButton(graph);

        graph.addListener(GraphEventType.SELECTION_CHANGED, data -> {
            boolean nodeSelected = graph.getSelectedNode() != null;
            boolean edgeSelected = graph.getSelectedEdge() != null;
            addEdge.setEnabled(nodeSelected);
            removeNode.setEnabled(nodeSelected);
            removeEdge.setEnabled(edgeSelected);
        });

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(addNode);
        menuBar.add(addEdge);
        menuBar.add(removeNode);
        menuBar.add(removeEdge);
        return menuBar;
    }

    /**
     * Creates the button that adds a new node to the graph.
     *
     * @param graph the graph model the button operates on
     * @return the configured button
     */
    private JButton createAddNodeButton(Graph graph) {
        JButton button = new JButton("Add Node");
        button.addActionListener(e -> {
            int id = graph.nextNodeId();
            graph.addNode(new Node(id, "Node " + id, 400, 300));
        });
        return button;
    }

    /**
     * Creates the button that starts adding an edge from the selected node.
     *
     * @param graph         the graph model the button operates on
     * @param mouseListener listener used to enter edge-adding mode
     * @return the configured button
     */
    private JButton createAddEdgeButton(Graph graph, GraphMouseListener mouseListener) {
        JButton button = new JButton("Add Edge");
        button.setEnabled(false);
        button.addActionListener(e -> {
            Node sel = graph.getSelectedNode();
            if (sel != null) {
                mouseListener.startAddingEdge(sel);
            }
        });
        return button;
    }

    /**
     * Creates the button that removes the selected node.
     *
     * @param graph the graph model the button operates on
     * @return the configured button
     */
    private JButton createRemoveNodeButton(Graph graph) {
        JButton button = new JButton("Remove Node");
        button.setEnabled(false);
        button.addActionListener(e -> {
            Node sel = graph.getSelectedNode();
            if (sel != null) {
                graph.deleteNode(sel.getId());
            }
        });
        return button;
    }

    /**
     * Creates the button that removes the selected edge.
     *
     * @param graph the graph model the button operates on
     * @return the configured button
     */
    private JButton createRemoveEdgeButton(Graph graph) {
        JButton button = new JButton("Remove Edge");
        button.setEnabled(false);
        button.addActionListener(e -> {
            Edge sel = graph.getSelectedEdge();
            if (sel != null) {
                graph.deleteEdge(sel.getId());
            }
        });
        return button;
    }

    /**
     * Subscribes the options panel to the model so it shows the details of the
     * currently selected node or edge, or a placeholder when nothing is selected.
     *
     * @param graph        the graph model to observe
     * @param optionsPanel the panel that displays element details
     */
    private void wireOptionsMenu(Graph graph, OptionsPanel optionsPanel) {
        graph.addListener(GraphEventType.SELECTION_CHANGED, data -> {
            if (graph.getSelectedNode() != null) {
                optionsPanel.showNodeMenu(graph.getSelectedNode());
            } else if (graph.getSelectedEdge() != null) {
                optionsPanel.showEdgeMenu(graph.getSelectedEdge());
            } else {
                optionsPanel.showNothingSelected();
            }
        });
    }

    /**
     * Creates the split view that combines the graph panel and the options panel.
     *
     * @param graphPanel   the graph canvas shown on the right
     * @param optionsPanel the options panel shown on the left
     * @return a split pane with the graph view on the left and the options panel on
     *         the right
     */
    private JSplitPane createSplitPane(GraphPanel graphPanel, OptionsPanel optionsPanel) {

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, optionsPanel, graphPanel);

        // Give the graph panel the majority of the horizontal space
        splitPane.setDividerLocation(220);

        return splitPane;
    }
}
