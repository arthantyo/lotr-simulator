package nl.rug.oop.rts.view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;

import nl.rug.oop.rts.model.Edge;
import nl.rug.oop.rts.model.Graph;
import nl.rug.oop.rts.model.GraphEventType;
import nl.rug.oop.rts.model.Node;
import nl.rug.oop.rts.controller.*;
import nl.rug.oop.rts.controller.command.*;

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
        CommandManager commandManager = new CommandManager();
        Graph graph = new Graph();
        GraphPanel graphPanel = new GraphPanel(graph, commandManager);
        OptionsPanel optionsPanel = new OptionsPanel(commandManager);
        optionsPanel.setOnNameChanged(graphPanel::repaint);

        wireOptionsMenu(graph, optionsPanel);
        setJMenuBar(createMenuBar(graph, graphPanel, commandManager));
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
     * @param graph      the graph model the buttons operate on
     * @param graphPanel the panel displaying the graph
     * @param commandManager manager that records reversible actions for undo/redo
     * @return the configured menu bar
     */
    private JMenuBar createMenuBar(Graph graph, GraphPanel graphPanel, CommandManager commandManager) {
        GraphMouseListener mouseListener = graphPanel.getMouseListener();
        JButton addNode = createAddNodeButton(graph, graphPanel, commandManager);
        JButton addEdge = createAddEdgeButton(graph, mouseListener);
        JButton removeNode = createRemoveNodeButton(graph, commandManager);
        JButton removeEdge = createRemoveEdgeButton(graph, commandManager);
        JButton undo = new JButton("Undo");
        JButton redo = new JButton("Redo");
        undo.setEnabled(false);
        redo.setEnabled(false);
        undo.addActionListener(e -> commandManager.undo());
        redo.addActionListener(e -> commandManager.redo());

        graph.addListener(GraphEventType.SELECTION_CHANGED, data -> {
            boolean nodeSelected = graph.getSelectedNode() != null;
            boolean edgeSelected = graph.getSelectedEdge() != null;
            addEdge.setEnabled(nodeSelected);
            removeNode.setEnabled(nodeSelected);
            removeEdge.setEnabled(edgeSelected);
        });

        commandManager.setOnChange(() -> {
            undo.setEnabled(commandManager.canUndo());
            redo.setEnabled(commandManager.canRedo());
            graphPanel.repaint();
        });

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(addNode);
        menuBar.add(addEdge);
        menuBar.add(removeNode);
        menuBar.add(removeEdge);
        menuBar.add(undo);
        menuBar.add(redo);
        return menuBar;
    }

    /**
     * Creates the button that adds a new node to the graph.
     *
     * @param graph      the graph model the button operates on
     * @param graphPanel the panel used to determine the initial position of the new
     *                   node
     * @param commandManager manager that records reversible actions for undo/redo
     * @return the configured button
     */
    private JButton createAddNodeButton(Graph graph, GraphPanel graphPanel, CommandManager commandManager) {
        JButton button = new JButton("Add Node");
        button.addActionListener(e -> {
            int id = graph.nextNodeId();

            int centerX = graphPanel.getWidth() / 2;
            int centerY = graphPanel.getHeight() / 2;

            int worldX = (int) Math.round(graphPanel.toWorldX(centerX));
            int worldY = (int) Math.round(graphPanel.toWorldY(centerY));

            commandManager.executeCommand(new AddNodeCommand(graph, new Node(id, "Node " + id, worldX, worldY)));
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
     * @param commandManager manager that records reversible actions for undo/redo
     * @return the configured button
     */
    private JButton createRemoveNodeButton(Graph graph, CommandManager commandManager) {
        JButton button = new JButton("Remove Node");
        button.setEnabled(false);
        button.addActionListener(e -> {
            Node sel = graph.getSelectedNode();
            if (sel != null) {
                commandManager.executeCommand(new DeleteNodeCommand(graph, sel));
            }
        });
        return button;
    }

    /**
     * Creates the button that removes the selected edge.
     *
     * @param graph the graph model the button operates on
     * @param commandManager manager that records reversible actions for undo/redo
     * @return the configured button
     */
    private JButton createRemoveEdgeButton(Graph graph, CommandManager commandManager) {
        JButton button = new JButton("Remove Edge");
        button.setEnabled(false);
        button.addActionListener(e -> {
            Edge sel = graph.getSelectedEdge();
            if (sel != null) {
                commandManager.executeCommand(new DeleteEdgeCommand(graph, sel));
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
