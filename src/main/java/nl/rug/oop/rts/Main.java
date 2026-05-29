package nl.rug.oop.rts;

import com.formdev.flatlaf.FlatDarculaLaf;

import nl.rug.oop.rts.view.MainFrame;

/**
 * Entry point of the RTS editor application.
 */
public class Main {

    /**
     * Starts the application and opens the main frame.
     *
     * @param args Commandline arguments.
     */
    public static void main(String[] args) {
        FlatDarculaLaf.setup(); // Dark mode
        new MainFrame().setVisible(true);
    }
}
