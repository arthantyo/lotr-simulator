package nl.rug.oop.rts.view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import java.awt.Dimension;

import nl.rug.oop.rts.model.Edge;
import nl.rug.oop.rts.model.Graph;
import nl.rug.oop.rts.model.GraphEventType;
import nl.rug.oop.rts.model.Node;

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
        setJMenuBar(createMenuBar(graph, graphPanel));
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
     * @param graphPanel    the panel displaying the graph
     * @return the configured menu bar
     */
    private JMenuBar createMenuBar(Graph graph, GraphPanel graphPanel) {
        GraphMouseListener mouseListener = graphPanel.getMouseListener();
        JButton addNode = createAddNodeButton(graph, graphPanel);
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
     * @param graphPanel the panel used to determine the initial position of the new node
     * @return the configured button
     */
    private JButton createAddNodeButton(Graph graph, GraphPanel graphPanel) {
        JButton button = new JButton("Add Node");
        button.addActionListener(e -> {
            int id = graph.nextNodeId();

            int centerX = graphPanel.getWidth() / 2;
            int centerY = graphPanel.getHeight() / 2;

            int worldX = (int) Math.round(graphPanel.toWorldX(centerX));
            int worldY = (int) Math.round(graphPanel.toWorldY(centerY));

            graph.addNode(new Node(id, "Node " + id, worldX, worldY));
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
                optionsPanel.showNodeMenu(graph, graph.getSelectedNode());
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
        JScrollPane optionsScrollPane = new JScrollPane(optionsPanel);
        optionsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        optionsScrollPane.setPreferredSize(new Dimension(390, getHeight()));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, optionsScrollPane, graphPanel);

        // Give the graph panel the majority of the horizontal space
        splitPane.setDividerLocation(390);

        return splitPane;
    }
}
