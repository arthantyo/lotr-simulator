package nl.rug.oop.rts.view;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import nl.rug.oop.rts.model.Army;
import nl.rug.oop.rts.model.Edge;
import nl.rug.oop.rts.model.Faction;
import nl.rug.oop.rts.model.Graph;
import nl.rug.oop.rts.model.Node;
import nl.rug.oop.rts.model.Unit;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

/**
 * Options panel of the application. Displays context-sensitive controls
 * depending on the currently selected graph element. Shows a default
 * message when nothing is selected, or an editing form for a node or edge.
 */
public class OptionsPanel extends JPanel {

    /**
     * Callback to invoke when an edge's name is changed, so the graph panel can
     * repaint.
     */
    private Runnable onNameChanged = () -> {
    };

    /**
     * Creates the options panel with a grey background and a placeholder
     * label shown when no graph element is selected.
     */
    public OptionsPanel() {
        setBackground(Color.GRAY);
        add(new JLabel("Nothing selected"));
    }

    /**
     * Sets the callback to be invoked when an edge's name is changed.
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

    private String getRandomUnitName(Faction faction, Random random) {
        String[] names;

        switch (faction) {
            case MEN:
                names = new String[] { "Gondor Soldier", "Tower Guard", "Ithilien Ranger" };
                break;
            case ELVES:
                names = new String[] { "Lorien Warrior", "Mirkwood Archer", "Rivendell Lancer" };
                break;
            case DWARVES:
                names = new String[] { "Guardian", "Phalanx", "Axe Thrower" };
                break;
            case MORDOR:
                names = new String[] { "Orc Warrior", "Orc Pikeman", "Haradrim Archer" };
                break;
            case ISENGARD:
                names = new String[] { "Uruk-hai", "Uruk Crossbowman", "Warg Rider" };
                break;
            default:
                names = new String[] { "Unknown Unit" };
                break;
        }

        return names[random.nextInt(names.length)];
    }

    private ArrayList<Unit> createRandomUnits(Faction faction) {
        Random random = new Random();
        ArrayList<Unit> units = new ArrayList<>();

        int unitCount = 10 + random.nextInt(41); // Random number of units between 10 and 50
        for (int i = 0; i < unitCount; i++) {
            int health = 20 + random.nextInt(31); // Health between 20 and 50
            String name = getRandomUnitName(faction, random);
            int damage = 5 + random.nextInt(16); // Damage between 5 and 20
            units.add(new Unit(name, damage, health));
        }
        return units;

    }

    /**
     * Replaces the panel contents with a node editing form.
     * Displays the node's name as an editable text field.
     * Pressing Enter in the name field commits the new name to the model.
     *
     * @param node the node whose details should be displayed and edited
     */
    public void showNodeMenu(Graph graph, Node node) {
        removeAll();

        add(new JLabel("Node name: "));

        JTextField nodeNameField = new JTextField(node.getName(), 15);
        add(nodeNameField);

        nodeNameField.addActionListener(e -> {
            node.setName(nodeNameField.getText());
            onNameChanged.run();
        });

        add(new JLabel("Armies: " + node.getArmies().size()));

        for (Army army : node.getArmies()) {
            add(new JLabel(army.getFaction() + " - " + army.getUnits().size() + " units"));
        }

        JButton addArmyButton = new JButton("Add Army");
        add(addArmyButton);

        addArmyButton.addActionListener(e -> {
            Faction faction = (Faction) JOptionPane.showInputDialog(
                    this,
                    "Select faction:",
                    "Add Army",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    Faction.values(),
                    Faction.MEN);

            if (faction == null) {
                return;
            }

            Army army = new Army(faction, createRandomUnits(faction));
            graph.addArmyToNode(node, army);

            showNodeMenu(graph, node);
        });

        JButton removeArmyButton = new JButton("Remove Army");
        add(removeArmyButton);

        removeArmyButton.addActionListener(e -> {
            if (node.getArmies().isEmpty()) {
                return;
            }

            Army armyToRemove = node.getArmies().get(node.getArmies().size() - 1);
            graph.removeArmyFromNode(node, armyToRemove);

            showNodeMenu(graph, node);
        });

        revalidate();
        repaint();
    }
}
