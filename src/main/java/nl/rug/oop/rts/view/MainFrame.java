package nl.rug.oop.rts.view;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import nl.rug.oop.rts.model.Edge;
import nl.rug.oop.rts.model.Graph;
import nl.rug.oop.rts.model.GraphEventType;
import nl.rug.oop.rts.model.Node;
import nl.rug.oop.rts.controller.*;
import nl.rug.oop.rts.controller.command.*;
import nl.rug.oop.rts.model.Simulation;

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
        JButton[] simButtons = createSimulationButtons(graph, commandManager);
        JButton startStop = simButtons[0];
        JButton stepForward = simButtons[1];
        JButton stepBack = simButtons[2];

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

        return buildMenuBar(addNode, addEdge, removeNode, removeEdge,
                undo, redo, stepBack, startStop, stepForward);
    }

    /**
     * Assembles a menu bar containing the given buttons in order.
     *
     * @param buttons the buttons to add to the menu bar
     * @return the assembled menu bar
     */
    private JMenuBar buildMenuBar(JButton... buttons) {
        JMenuBar menuBar = new JMenuBar();
        for (JButton button : buttons) {
            menuBar.add(button);
        }
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
     * Hides the step forward and step back buttons and resets the start/stop button text.
     *
     * @param startStop the button that starts/stops the simulation
     * @param stepForward the button that steps the simulation forward
     * @param stepBack the button that steps the simulation backward
     */
    private void hideSimulationButtons(JButton startStop, JButton stepForward, JButton stepBack) {
        startStop.setText("Start Simulation");
        stepForward.setVisible(false);
        stepBack.setVisible(false);
    }

    /**
     * Creates the buttons that control the simulation and wires them to the model.
     * @param graph the graph model the buttons operate on
     * @param commandManager manager whose edit history is cleared on simulation start/end
     * @return the configured buttons in an array: [start/stop button, step forward button, step back button]
     */
    private JButton[] createSimulationButtons(Graph graph, CommandManager commandManager) {
        JButton startStop = new JButton("Start Simulation");
        JButton stepForward = new JButton("▶");
        JButton stepBack = new JButton("◀");
        hideSimulationButtons(startStop, stepForward, stepBack);

        Simulation sim = new Simulation(graph);
        var isRunning = new java.util.concurrent.atomic.AtomicBoolean(false);

        startStop.addActionListener(e -> toggleSimulation(
                graph, sim, commandManager, isRunning, startStop, stepForward, stepBack));

        stepBack.addActionListener(e -> {
            sim.subtractTime(graph);
            repaint();
        });

        stepForward.addActionListener(e -> {
            sim.advanceTime(graph);
            repaint();
        });
  
        return new JButton[]{ startStop, stepForward, stepBack };
    }

    /**
     * Toggles the simulation between running and stopped. Updates the start/stop
     * button text, hides the stepping buttons and clears the undo/redo history,
     * since starting or ending a simulation resets the graph state.
     *
     * @param graph          the graph being simulated
     * @param sim            the simulation controlling the graph
     * @param commandManager manager whose edit history is cleared
     * @param isRunning      flag tracking whether the simulation is running
     * @param startStop      the start/stop button
     * @param stepForward    the step-forward button
     * @param stepBack       the step-back button
     */
    private void toggleSimulation(Graph graph, Simulation sim, CommandManager commandManager,
            java.util.concurrent.atomic.AtomicBoolean isRunning,
            JButton startStop, JButton stepForward, JButton stepBack) {
        if (!isRunning.get()) {
            isRunning.set(true);
            startStop.setText("End Simulation");
            sim.startSimulation(graph);
        } else {
            isRunning.set(false);
            startStop.setText("Start Simulation");
            sim.endSimulation(graph);
        }
        hideSimulationButtons(startStop, stepForward, stepBack);
        commandManager.clear();
        repaint();
    }

    /**
     * Subscribes the options panel to the model so it shows the details of the
     *  selected node or edge.
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

        // Rebuild the node menu when armies change (e.g. on undo/redo of army actions),
        // so the displayed army list stays in sync with the model.
        graph.addListener(GraphEventType.ARMIES_CHANGED, data -> {
            if (graph.getSelectedNode() != null) {
                optionsPanel.showNodeMenu(graph, graph.getSelectedNode());
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
