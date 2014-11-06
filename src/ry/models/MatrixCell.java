package ry.models;

import java.io.Serializable;

/**
 * Zelltyp
 * @author cnhonker
 */
public class MatrixCell implements Serializable {
    
    /**
     *
     * @param celltype
     * @param posX
     * @param posY
     */
    public MatrixCell(CellProperty celltype, int posX, int posY) {
        prop = celltype;
        x = posX;
        y = posY;
    }
    
    public CellProperty getCellProp() {
        return prop;
    }
    
    public void setCellProp(CellProperty cellProp) {
        prop = cellProp;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    private CellProperty prop;
    private final int x;
    private final int y;
}