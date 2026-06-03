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
 * depending on the currently selected graph element.
 */
public class OptionsPanel extends JPanel {

    /**
     * Callback to invoke when a name is changed, so the graph panel can repaint.
     */
    private Runnable onNameChanged = () -> {
    };

    /**
     * Creates the options panel with a placeholder label.
     */
    public OptionsPanel() {
        setBackground(Color.GRAY);
        add(new JLabel("Nothing selected"));
    }

    /**
     * Sets the callback to be invoked when a name is changed.
     *
     * @param onNameChanged the callback to invoke when a name is changed
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
     * Displays the edge editing menu.
     *
     * @param edge the edge whose details should be displayed and edited
     */
    public void showEdgeMenu(Edge edge) {
        removeAll();

        add(new JLabel("Edge Name:"));
        JTextField nameField = new JTextField(edge.getName(), 15);
        add(nameField);

        add(new JLabel("From: " + edge.getFrom().getName()));
        add(new JLabel("To: " + edge.getTo().getName()));

        nameField.addActionListener(e -> {
            edge.setName(nameField.getText());
            onNameChanged.run();
        });

        revalidate();
        repaint();
    }

    /**
     * Selects a random unit name for the given faction.
     *
     * @param faction faction whose unit names may be used
     * @param random  random number generator
     * @return random unit name
     */
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

    /**
     * Creates random units for a new army.
     *
     * @param faction faction used to select unit names
     * @return list of random units
     */
    private ArrayList<Unit> createRandomUnits(Faction faction) {
        Random random = new Random();
        ArrayList<Unit> units = new ArrayList<>();

        int unitCount = 10 + random.nextInt(41);
        for (int i = 0; i < unitCount; i++) {
            int health = 20 + random.nextInt(31);
            String name = getRandomUnitName(faction, random);
            int damage = 5 + random.nextInt(16);

            units.add(new Unit(name, damage, health));
        }

        return units;
    }

    /**
     * Displays the node editing menu.
     *
     * @param graph graph model used to add or remove armies
     * @param node  the node whose details should be displayed and edited
     */
    public void showNodeMenu(Graph graph, Node node) {
        removeAll();

        addNodeNameField(node);
        addArmyDetails(node);
        addArmyButtons(graph, node);

        revalidate();
        repaint();
    }

    /**
     * Adds the editable node name field to the panel.
     *
     * @param node node whose name is edited
     */
    private void addNodeNameField(Node node) {
        add(new JLabel("Node name:"));

        JTextField nodeNameField = new JTextField(node.getName(), 15);
        add(nodeNameField);

        nodeNameField.addActionListener(e -> {
            node.setName(nodeNameField.getText());
            onNameChanged.run();
        });
    }

    /**
     * Adds labels describing the armies currently on the node.
     *
     * @param node node whose armies are displayed
     */
    private void addArmyDetails(Node node) {
        add(new JLabel("Armies: " + node.getArmies().size()));

        for (Army army : node.getArmies()) {
            add(new JLabel(army.getTeam() + " - " + army.getUnits().size() + " units"));
        }
    }

    /**
     * Adds army management buttons to the node menu.
     *
     * @param graph graph model used to update armies
     * @param node node whose armies are managed
     */
    private void addArmyButtons(Graph graph, Node node) {
        JButton addArmyButton = new JButton("Add Army");
        add(addArmyButton);
        addArmyButton.addActionListener(e -> addArmy(graph, node));

        JButton removeArmyButton = new JButton("Remove Army");
        removeArmyButton.setEnabled(!node.getArmies().isEmpty());
        add(removeArmyButton);
        removeArmyButton.addActionListener(e -> removeLastArmy(graph, node));
    }

    /**
     * Prompts for a faction and adds a new army to the node.
     *
     * @param graph graph model used to add the army
     * @param node node that receives the army
     */
    private void addArmy(Graph graph, Node node) {
        Faction faction = chooseFaction();
        if (faction == null) {
            return;
        }

        Army army = new Army(faction, createRandomUnits(faction));
        graph.addArmyToNode(node, army);
        showNodeMenu(graph, node);
    }

    /**
     * Shows a faction selection dialog.
     *
     * @return selected faction, or null when cancelled
     */
    private Faction chooseFaction() {
        return (Faction) JOptionPane.showInputDialog(
                this,
                "Select faction:",
                "Add Army",
                JOptionPane.PLAIN_MESSAGE,
                null,
                Faction.values(),
                Faction.MEN);
    }

    /**
     * Removes the most recently added army from the node.
     *
     * @param graph graph model used to remove the army
     * @param node node whose army is removed
     */
    private void removeLastArmy(Graph graph, Node node) {
        if (node.getArmies().isEmpty()) {
            return;
        }

        Army armyToRemove = node.getArmies().get(node.getArmies().size() - 1);
        graph.removeArmyFromNode(node, armyToRemove);
        showNodeMenu(graph, node);
    }
}
