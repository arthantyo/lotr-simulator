package nl.rug.oop.rts.view;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import nl.rug.oop.rts.model.Edge;
import nl.rug.oop.rts.model.Node;

import java.awt.Color;

/**
 * Options panel of the application. Displays context-sensitive controls
 * depending on the currently selected graph element. Shows a default
 * message when nothing is selected, or an editing form for a node or edge.
 */
public class OptionsPanel extends JPanel {

    /**
     * Callback to invoke when an edge's name is changed, so the graph panel can repaint.
     */
    private Runnable onNameChanged = () -> {};

    /**
     * Creates the options panel with a grey background and a placeholder
     * label shown when no graph element is selected.
     */
    public OptionsPanel() {
        setBackground(Color.GRAY);
        add(new JLabel("Nothing selected"));
    }

    /** Sets the callback to be invoked when an edge's name is changed. 
     * 
     * @param onNameChanged the callback to invoke when an edge's name is changed
    */
    public void setOnNameChanged(Runnable onNameChanged) {
        this.onNameChanged = onNameChanged;
    }

    /**
     * Displays the default message when no graph element is selected.
     */
    public void showNothingSelected() {
        removeAll();
        add(new JLabel("Nothing selected"));
        revalidate();
        repaint();
    }

    /**
     * 
     * @param edge
     */
    /**
     * Replaces the panel contents with an edge editing form.
     * Displays the edge's name as an editable text field, and shows
     * the names of the source and destination nodes as read-only labels.
     * Pressing Enter in the name field commits the new name to the model.
     *
     * @param edge the edge whose details should be displayed and edited
     */
    public void showEdgeMenu(Edge edge) {
        // Clear any previously displayed component
        removeAll();

        add(new JLabel("Edge Name:"));
        JTextField nameField = new JTextField(edge.getName(), 15);
        add(nameField);

        // Read-only labels showing which nodes this edge connects
        add(new JLabel("From: " + edge.getFrom().getName()));
        add(new JLabel("To: " + edge.getTo().getName()));

        // Commit the edited name to the model when the user presses Enter
        nameField.addActionListener(e -> {
            edge.setName(nameField.getText());
            onNameChanged.run();
        });

        revalidate();
        repaint();
    }

    /**
     * Replaces the panel contents with a node editing form.
     * Displays the node's name as an editable text field.
     * Pressing Enter in the name field commits the new name to the model.
     *
     * @param node the node whose details should be displayed and edited
     */
    public void showNodeMenu(Node node) {
        // Clear any previously displayed component
        removeAll();
        add(new JLabel("Node names: "));

        JTextField nodeNameField = new JTextField(node.getName(), 15);
        add(nodeNameField);

        // Commit the edited name to the model when the user presses Enter
        nodeNameField.addActionListener(e -> {
            node.setName(nodeNameField.getText());
            onNameChanged.run();
        });
        revalidate();
        repaint();
    }
}
