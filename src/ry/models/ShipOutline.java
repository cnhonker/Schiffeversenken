package ry.models;

import java.util.Random;
import java.awt.Point;
import java.io.Serializable;
import java.util.LinkedList;
import static ry.models.CellProperty.DEFAULT;
import static ry.models.CellProperty.DESTROYED;
import static ry.models.CellProperty.SHIP;
import static ry.models.CellProperty.POINT;
import static ry.models.ShipProperty.BATTLESHIP;
import static ry.models.ShipProperty.CRUISER;
import static ry.models.ShipProperty.DESTROYER;
import static ry.models.ShipProperty.UBOOT;

/**
 *
 * @author cnhonker
 */
public class ShipOutline implements Serializable {

    public ShipOutline(int width) {
        fieldWidth = width;
    }

    /**
     * Ob die Platzierung gültig ist
     * @return
     */
    public boolean valid() {
        return isValid;
    }

    /**
     * Ob alle Schiffe platziert worden sind
     * @return
     */
    public boolean allplaced() {
        return ships.size() == 10;
    }

    public boolean status() {
        return ships.size() == 0;
    }

    /**
     * Noch Schiffe übrig
     * @return
     */
    public boolean shipsCleared() {
        boolean result = true;
        for (Ship ship : ships) {
            if (!ship.isDestroyed()) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * Füge Schiffe zur Liste hinzu
     * @param ship
     */
    public void addShipToList(Ship ship) {
        switch (ship.getShipProperty()) {
            case BATTLESHIP:
                if (bShip < 1) {
                    ships.add(ship);
                    bShip += 1;
                    validate();
                }
                break;
            case CRUISER:
                if (cShip < 2) {
                    ships.add(ship);
                    cShip += 1;
                    validate();
                }
                break;
            case DESTROYER:
                if (dShip < 3) {
                    ships.add(ship);
                    dShip += 1;
                    validate();
                }
                break;
            case UBOOT:
                if (uShip < 4) {
                    ships.add(ship);
                    uShip += 1;
                    validate();
                }
                break;
        }
    }

    /**
     *
     * @return
     */
    public Matrix getUnderLineMatrix() {
        Matrix result = new Matrix(fieldWidth);
        for (Ship ship : ships) {
            LinkedList<MatrixCell> shipsCells = ship.getShipCells();
            for (MatrixCell cell : shipsCells) {
                CellProperty customCellType = (cell.getCellProp() == SHIP) ? DEFAULT : cell.getCellProp();
                result.setCellProp(cell.getX(), cell.getY(), customCellType);
            }
        }
        for (Point splash : cross) {
            result.setCellProp(splash.x, splash.y, POINT);
        }
        return result;
    }

    /**
     * UNDO - zuletzte platzierte Schiff in der Liste entfernen
     */
    public void removeLastShip() {
        if (!ships.isEmpty()) {
            ShipProperty removedShipType = ships.getLast().getShipProperty();
            switch (removedShipType) {
                case BATTLESHIP:
                    bShip--;
                    break;
                case CRUISER:
                    cShip--;
                    break;
                case DESTROYER:
                    dShip--;
                    break;
                case UBOOT:
                    uShip--;
                    break;
            }
            ships.removeLast();
        }
    }

    /**
     * RESET - Alles löschen
     */
    public void clear() {
        ships.clear();
        bShip = 0;
        cShip = 0;
        dShip = 0;
        uShip = 0;
        validate();
    }

    /**
     * BEREIT: Prüfung ob alle Schiffe gesetzt wurden
     * @param prop
     * @return
     */
    public boolean userReady(ShipProperty prop) {
        switch (prop) {
            case BATTLESHIP:
                return bShip == 1;
            case CRUISER:
                return cShip == 2;
            case DESTROYER:
                return dShip == 3;
            case UBOOT:
                return uShip == 4;
            default:
                return false;
        }
    }

    /**
     * Zufallsgenerator
     */
    public void random() {
        clear();
        Random gen = new Random();

        while (!allplaced()) {
            boolean shipAdded = false;
            while (!shipAdded) {
                Point start = new Point(gen.nextInt(fieldWidth), gen.nextInt(fieldWidth));
                boolean horizontal = gen.nextBoolean();
                Point end;
                ShipProperty type;
                if (ships.size() < 1) {
                    type = BATTLESHIP;
                    end = placeBShip(horizontal, start);
                } else if (ships.size() < 1
                                          + 2) {
                    type = CRUISER;
                    end = placeCShip(horizontal, start);
                } else if (ships.size() < 1 + 2 + 3) {
                    type = DESTROYER;
                    end = placeDShip(horizontal, start);
                } else {
                    type = UBOOT;
                    end = new Point(start);
                }

                addShipToList(new Ship(type, start, end));
                shipAdded = valid();
                if (!shipAdded) {
                    removeLastShip();
                }
            }
        }
        validate();
    }

    /**
     * Destroyer platzieren
     * @param horizontal
     * @param start
     * @return 
     */
    private Point placeDShip(boolean horizontal, Point start) {
        Point end;
        if (horizontal) {
            end = new Point(start.x + 1, start.y);
        } else {
            end = new Point(start.x, start.y + 1);
        }
        return end;
    }

    /**
     * Cruiser platzieren
     * @param horizontal
     * @param start
     * @return 
     */
    private Point placeCShip(boolean horizontal, Point start) {
        Point end;
        if (horizontal) {
            end = new Point(start.x + 2, start.y);
        } else {
            end = new Point(start.x, start.y + 2);
        }
        return end;
    }

    /**
     * Battleship platzieren
     * @param horizontal
     * @param start
     * @return 
     */
    private Point placeBShip(boolean horizontal, Point start) {
        Point end;
        if (horizontal) {
            end = new Point(start.x + 3, start.y);
        } else {
            end = new Point(start.x, start.y + 3);
        }
        return end;
    }

    /**
     *
     * @return
     */
    public Matrix getMatrix() {
        Matrix result = new Matrix(fieldWidth);
        for (Ship ship : ships) {
            LinkedList<MatrixCell> shipsCells = ship.getShipCells();
            for (MatrixCell cell : shipsCells) {
                result.setCellProp(cell.getX(), cell.getY(), cell.getCellProp());
            }
        }
        for (Point c : cross) {
            result.setCellProp(c.x, c.y, POINT);
        }
        return result;
    }

    public CellProperty getCellProp(int x, int y) {
        return getMatrix().getCellProp(x, y);
    }

    /**
     *
     * @param prop
     * @param x
     * @param y
     */
    public void setCellProp(CellProperty prop, int x, int y) {
        if (x < 0 || x >= fieldWidth || y < 0 || y >= fieldWidth) {
            return;
        }
        if (prop == POINT) {
            cross.add(new Point(x, y));
        } else if (prop == DESTROYED) {
            for (Ship ship : ships) {
                if (ship.partOf(x, y)) {
                    ship.setCellProp(prop, x, y);
                    if (ship.isDestroyed()) {
                        LinkedList<Point> nearPoints = ship.getNearPoints();
                        for (Point point : nearPoints) {
                            cross.add(point);
                        }
                    }
                }
            }
        }
    }

    private void validate() {
        isValid = true;
        byte field[][] = new byte[fieldWidth][fieldWidth];
        for (byte i = 0; i < fieldWidth; i++) {
            for (byte j = 0; j < fieldWidth; j++) {
                field[i][j] = 13;
            }
        }
        for (Ship ship : ships) {
            LinkedList<Point> loc = ship.getShipPoints();
            for (Point point : loc) {
                if (point.x < 0 || point.x >= fieldWidth
                    || point.y < 0 || point.y >= fieldWidth
                    || field[point.x][point.y] == 1
                    || field[point.x][point.y] == 2) {
                    isValid = false;
                    return;
                }
                field[point.x][point.y] = 2;
            }
            LinkedList<Point> side = ship.getNearPoints();
            for (Point point : side) {
                if (point.x >= 0 && point.x < fieldWidth && point.y >= 0
                    && point.y < fieldWidth) {
                    field[point.x][point.y] = 1;
                }
            }
        }
    }

    private final LinkedList<Point> cross = new LinkedList<>();
    private final LinkedList<Ship> ships = new LinkedList<>();
    private final int fieldWidth;
    private int bShip = 0;
    private int cShip = 0;
    private int dShip = 0;
    private int uShip = 0;
    private boolean isValid = false;

}
