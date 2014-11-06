package ry;

import java.awt.EventQueue;
import ry.models.GameModel;

/**
 * Main-Klasse
 * @author cnhonker
 */
public class JStart {

    /**
     * Zum Main-Methode
     * @param args
     */
    public static void main(String args[]) {

        /**
         * Java Swing Single-Threaded Model Launcher
         */
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Battleship view = new Battleship(new GameModel());
                view.setVisible(true);
            }
        });
    }
}
