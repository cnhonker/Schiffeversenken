package ry.models;

import java.awt.Point;
import java.util.LinkedList;
import java.io.Serializable;

import static java.lang.Math.signum;
import static ry.models.CellProperty.SHIP;
import static ry.models.ShipProperty.UBOOT;

/**
 *
 * @author cnhonker
 */
public class Ship implements Serializable {

    /**
     *
     * @param shipProp
     * @param start
     * @param end
     */
    public Ship(ShipProperty shipProp, Point start, Point end) {
        prop = shipProp;
        if (start.x == end.x) {
            int x = start.x;
            for (int y = start.y; y != end.y; y += (int) signum(end.y - start.y)) {
                cellList.add(new MatrixCell(SHIP, x, y));
            }
            cellList.add(new MatrixCell(SHIP, x, end.y));
        } else {
            int y = start.y;
            for (int x = start.x; x != end.x; x += (int) signum(end.x - start.x)) {
                cellList.add(new MatrixCell(SHIP, x, y));
            }
            cellList.add(new MatrixCell(SHIP, end.x, y));
        }
    }

    public ShipProperty getShipProperty() {
        return prop;
    }

    public LinkedList<Point> getShipPoints() {
        LinkedList<Point> result = new LinkedList<>();
        for (MatrixCell cell : cellList) {
            result.add(new Point(cell.getX(), cell.getY()));
        }
        return result;
    }

    public LinkedList<MatrixCell> getShipCells() {
        return cellList;
    }
    
    public boolean partOf(int x, int y) {
        for (MatrixCell cell : cellList) {
            if (cell.getX() == x && cell.getY() == y) {
                return true;
            }
        }
        return false;
    }

    public LinkedList<Point> getNearPoints() {
        LinkedList<Point> save = new LinkedList<>();

        if (prop == UBOOT) {
            int cellX = cellList.getFirst().getX();
            int cellY = cellList.getFirst().getY();
            for (int x = cellX - 1; x <= cellX + 1; x++) {
                save.add(new Point(x, cellY - 1));
                save.add(new Point(x, cellY + 1));
            }
            save.add(new Point(cellX - 1, cellY));
            save.add(new Point(cellX + 1, cellY));
        } else if (cellList.getFirst().getX() == cellList.getLast().getX()) {
            int x = cellList.getFirst().getX();
            for (MatrixCell cell : cellList) {
                save.add(new Point(x - 1, cell.getY()));
                save.add(new Point(x + 1, cell.getY()));
            }
            int leftShift = cellList.get(1).getY() - cellList.get(0).getY();
            save.add(new Point(x - 1, cellList.getFirst().getY() - leftShift));
            save.add(new Point(x, cellList.getFirst().getY() - leftShift));
            save.add(new Point(x + 1, cellList.getFirst().getY() - leftShift));
            save.add(new Point(x - 1, cellList.getLast().getY() + leftShift));
            save.add(new Point(x, cellList.getLast().getY() + leftShift));
            save.add(new Point(x + 1, cellList.getLast().getY() + leftShift));
        } else {
            int y = cellList.getFirst().getY();
            for (MatrixCell cell : cellList) {
                save.add(new Point(cell.getX(), y - 1));
                save.add(new Point(cell.getX(), y + 1));
            }
            int rightShift = cellList.get(1).getX() - cellList.get(0).getX();
            save.add(new Point(cellList.getFirst().getX() - rightShift, y - 1));
            save.add(new Point(cellList.getFirst().getX() - rightShift, y));
            save.add(new Point(cellList.getFirst().getX() - rightShift, y + 1));
            save.add(new Point(cellList.getLast().getX() + rightShift, y - 1));
            save.add(new Point(cellList.getLast().getX() + rightShift, y));
            save.add(new Point(cellList.getLast().getX() + rightShift, y + 1));
        }
        return save;
    }
    
    public boolean isDestroyed() {
        boolean result = true;
        for (MatrixCell cell : cellList) {
            if (cell.getCellProp() == SHIP) {
                result = false;
                break;
            }
        }
        return result;
    }

    public void setCellProp(CellProperty prop, int x, int y) {
        for (MatrixCell cell : cellList) {
            if (cell.getX() == x && cell.getY() == y) {
                cell.setCellProp(prop);
            }
        }
    }

    private final ShipProperty prop;
    private final LinkedList<MatrixCell> cellList = new LinkedList<>();
}
