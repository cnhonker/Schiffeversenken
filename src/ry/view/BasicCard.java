package ry.view;

import ry.Battleship;
import javax.swing.JPanel;

import ry.models.GameModelState;

/**
 * Basisklasse für die einzelne Views
 *
 * @author cnhonker
 */
public abstract class BasicCard extends JPanel {

    /**
     * Jeder View muss diese Update-Methode implementieren.
     * @param state - Aktuelle Status des Models
     */
    public abstract void update(GameModelState state);

    /**
     *
     * @param view
     */
    public BasicCard(Battleship view) {
        origin = view;
    }

    /**
     *
     * @return
     */
    public Battleship getOrigin() {
        return origin;
    }

    /**
     *
     */
    protected final Battleship origin;
}
