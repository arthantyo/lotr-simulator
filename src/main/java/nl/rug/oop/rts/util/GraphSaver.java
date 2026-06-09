package nl.rug.oop.rts.util;

import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import nl.rug.oop.rts.model.Graph;

/**
 * Lets the user pick a destination file and saves the JSON representation of a
 * {@link Graph} to it, ensuring the file ends with a {@code .json} extension.
 */
public class GraphSaver {

    /**
     * Shows a save dialog and, once a destination is chosen, writes the JSON
     * representation of the graph to the selected file. Does nothing if the user
     * cancels the dialog.
     *
     * @param parent the component the dialogs are anchored to
     * @param graph  the graph to save
     */
    public void save(Component parent, Graph graph) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Simulation");
        chooser.setFileFilter(new FileNameExtensionFilter("JSON files", "json"));

        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = withJsonExtension(chooser.getSelectedFile());
        String json = new GraphSerializer().serialize(graph);
        writeToFile(parent, file, json);
    }

    /**
     * Returns a file that is guaranteed to end with the {@code .json} extension,
     * appending it when the chosen file does not already have it.
     *
     * @param file the file selected by the user
     * @return a file ending in {@code .json}
     */
    private File withJsonExtension(File file) {
        String path = file.getAbsolutePath();
        if (path.toLowerCase().endsWith(".json")) {
            return file;
        }
        return new File(path + ".json");
    }

    /**
     * Writes the JSON text to the given file, showing an error dialog if the
     * write fails.
     *
     * @param parent the component the error dialog is anchored to
     * @param file   the destination file
     * @param json   the JSON text to write
     */
    private void writeToFile(Component parent, File file, String json) {
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(json);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent,
                    "Failed to save the simulation: " + ex.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
