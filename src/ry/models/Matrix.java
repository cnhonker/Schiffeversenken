package ry.models;

import java.io.Serializable;
import static ry.models.CellProperty.DEFAULT;

/**
 * Positionen der Schiffe speichern
 * @author cnhonker
 */
public class Matrix implements Serializable{

    /**
     *
     * @param size
     */
    public Matrix(int size) {
        arraySize = (size > 0) ? size : 10;
        matrix2D = new CellProperty[arraySize][arraySize];
        for (int i = 0; i < arraySize; i++) {
            for (int j = 0; j < arraySize; j++) {
                matrix2D[i][j] = DEFAULT;
            }
        }
    }

    /**
     * Schifftyp an der Position X,Y holen
     * @param x
     * @param y
     * @return
     */
    public CellProperty getCellProp(int x, int y) {
        if (x >= 0 && x < arraySize && y >= 0 && y < arraySize) {
            return matrix2D[x][y];
        } else {
            return null;
        }
    }

    /**
     * Schifftyp setzen
     * @param x
     * @param y
     * @param prop
     */
    public void setCellProp(int x, int y, CellProperty prop) {
        if (x >= 0 && x < arraySize && y >= 0 && y < arraySize) {
            matrix2D[x][y] = prop;
        }
    }

    private final int arraySize;
    private CellProperty matrix2D[][] = null;
}
