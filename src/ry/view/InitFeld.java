package ry.view;

import java.awt.Color;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import ry.models.ShipProperty;
import ry.network.Message;
import static ry.network.MessageProperty.USERACTION_SHIPMOVED;
import static ry.network.MessageProperty.USER_SHIPPLACED;
import static ry.models.ShipProperty.NONE;

/**
 * Platzieren der Schiffe
 *
 * @author cnhonker
 */
public class InitFeld extends PlaygroundUser implements MouseInputListener {

    public InitFeld(BasicCard view, int width) {
        super(view, width);
        initListener();
        shipColor = valid;
    }

    /**
     * Mousebewegungen tracken und entsprechend interpretieren
     */
    private void initListener() {
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (center != null) {
            drawShip(g);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (shipType != NONE && center != null) {
            if (SwingUtilities.isRightMouseButton(e)) {
                isHorizontal = !isHorizontal;
                updateCenter(e);
                updateColor(e);
                repaint();
            } else if (SwingUtilities.isLeftMouseButton(e)) {
                Object request[] = new Object[3];                               // Schiffe erzeugen
                request[0] = shipType;
                request[1] = getEndPoints().get(0);
                request[2] = getEndPoints().get(1);
                parent.getOrigin().getAdapter().send(new Message(USER_SHIPPLACED, request));
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        center = new Point();
        updateCenter(e);
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        center = null;
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        updateCenter(e);
        updateColor(e);
        repaint();
    }

    /**
     * Farbe des Schiffe setzen. Wenn es eine gültige Platzierung ist, dann die
     * Farbe valid nehmen sonst invalid
     *
     * @param isValid
     */
    public void setColor(boolean isValid) {
        if (isValid) {
            shipColor = valid;
        } else {
            shipColor = invalid;
        }
    }

    /**
     * Type des Schiffes setzen
     *
     * @param prop
     */
    public void setType(ShipProperty prop) {
        shipType = prop;
        isHorizontal = true;
    }

    /**
     * Orientierung wieder horizontal setzen Standard ist horizontal
     */
    public void resetOrientation() {
        isHorizontal = true;
    }

    /**
     * Farbe des Schiffes aktualisieren
     *
     * @param e
     */
    private void updateColor(MouseEvent e) {
        if (shipType != NONE) {
            Object request[] = new Object[3];                                   // Wenn die Position der Schiffe ändert
            request[0] = shipType;
            request[1] = getEndPoints().get(0);
            request[2] = getEndPoints().get(1);
            parent.getOrigin().getAdapter().send(new Message(USERACTION_SHIPMOVED, request));
        }
    }

    /**
     * Zentrum des Schiffes neuberechen
     * 
     * @param e
     */
    private void updateCenter(MouseEvent e) {
        if (shipType != NONE) {
            int leftBound, rightBound;
            int upperBound, lowerBound;
            int margin = margin();
            if (isHorizontal) {                                                 // Die Begrenzung des Schiffes
                leftBound = startX + margin;
                rightBound = endX - margin;
                upperBound = startY + cellSize / 2;
                lowerBound = endY - cellSize / 2;
            } else {
                leftBound = startX + cellSize / 2;
                rightBound = endX - cellSize / 2;
                upperBound = startY + margin;
                lowerBound = endY - margin;
            }
            setCenterX(e, leftBound, rightBound);
            setCenterY(e, upperBound, lowerBound);
        }
    }
    
    /**
     * Schifflänge / 2
     * @return 
     */
    private int margin() {
        int margin;
        switch (shipType) {
            case BATTLESHIP:
                margin = cellSize * 2;
                break;
            case CRUISER:
                margin = cellSize * 3 / 2;
                break;
            case DESTROYER:
                margin = cellSize;
                break;
            case UBOOT:
                margin = cellSize / 2;
                break;
            default:
                margin = 0;
        }
        return margin;
    }

    /**
     * Punkt Y des Zentrums 
     * @param e
     * @param upperBound
     * @param lowerBound 
     */
    private void setCenterY(MouseEvent e, int upperBound, int lowerBound) {
        if (e.getY() < upperBound) {
            center.y = upperBound;
        } else if (e.getY() > lowerBound) {
            center.y = lowerBound;
        } else {
            center.y = e.getY();
        }
    }

    /**
     * Punkt X des Zentrums
     * @param e
     * @param leftBound
     * @param rightBound 
     */
    private void setCenterX(MouseEvent e, int leftBound, int rightBound) {
        if (e.getX() < leftBound) {
            center.x = leftBound;
        } else if (e.getX() > rightBound) {
            center.x = rightBound;
        } else {
            center.x = e.getX();
        }
    }

    /**
     * Schiffe zeichnen
     *
     * @param g
     */
    private void drawShip(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Stroke tempStroke = g2.getStroke();
        BasicStroke stroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke);
        g2.setPaint(shipColor);

        switch (shipType) {
            case BATTLESHIP:
                drawBattleship(g2);
                break;
            case CRUISER:
                drawCruiser(g2);
                break;
            case DESTROYER:
                drawDestroyer(g2);
                break;
            case UBOOT:
                drawUboot(g2);
                break;
            default:
                break;
        }
        g2.setStroke(tempStroke);
    }

    private void drawUboot(Graphics2D g2) {
        g2.fillRect(center.x - cellSize / 2, center.y - cellSize / 2, cellSize, cellSize);
        g2.setPaint(border);
        g2.drawRect(center.x - cellSize / 2, center.y - cellSize / 2, cellSize, cellSize);
    }

    private void drawDestroyer(Graphics2D g2) {
        if (isHorizontal) {
            g2.fillRect(center.x - cellSize, center.y - cellSize / 2, cellSize * 2, cellSize);
            g2.setPaint(border);
            g2.drawRect(center.x - cellSize, center.y - cellSize / 2, cellSize, cellSize);
            g2.drawRect(center.x, center.y - cellSize / 2, cellSize, cellSize);
        } else {
            g2.fillRect(center.x - cellSize / 2, center.y - cellSize, cellSize, cellSize * 2);
            g2.setPaint(border);
            g2.drawRect(center.x - cellSize / 2, center.y - cellSize, cellSize, cellSize);
            g2.drawRect(center.x - cellSize / 2, center.y, cellSize, cellSize);
        }
    }

    private void drawCruiser(Graphics2D g2) {
        if (isHorizontal) {
            g2.fillRect(center.x - cellSize * 3 / 2, center.y - cellSize / 2, cellSize * 3, cellSize);
            g2.setPaint(border);
            g2.drawRect(center.x - cellSize * 3 / 2, center.y - cellSize / 2, cellSize, cellSize);
            g2.drawRect(center.x - cellSize / 2, center.y - cellSize / 2, cellSize, cellSize);
            g2.drawRect(center.x + cellSize / 2, center.y - cellSize / 2, cellSize, cellSize);
        } else {
            g2.fillRect(center.x - cellSize / 2, center.y - cellSize * 3 / 2, cellSize, cellSize * 3);
            g2.setPaint(border);
            g2.drawRect(center.x - cellSize / 2, center.y - cellSize * 3 / 2, cellSize, cellSize);
            g2.drawRect(center.x - cellSize / 2, center.y - cellSize / 2, cellSize, cellSize);
            g2.drawRect(center.x - cellSize / 2, center.y + cellSize / 2, cellSize, cellSize);
        }
    }

    private void drawBattleship(Graphics2D g2) {
        if (isHorizontal) {
            g2.fillRect(center.x - cellSize * 2, center.y - cellSize / 2, cellSize * 4, cellSize);
            g2.setPaint(border);
            g2.drawRect(center.x - cellSize * 2, center.y - cellSize / 2, cellSize, cellSize);
            g2.drawRect(center.x - cellSize, center.y - cellSize / 2, cellSize, cellSize);
            g2.drawRect(center.x, center.y - cellSize / 2, cellSize, cellSize);
            g2.drawRect(center.x + cellSize, center.y - cellSize / 2, cellSize, cellSize);
        } else {
            g2.fillRect(center.x - cellSize / 2, center.y - cellSize * 2, cellSize, cellSize * 4);
            g2.setPaint(border);
            g2.drawRect(center.x - cellSize / 2, center.y - cellSize * 2, cellSize, cellSize);
            g2.drawRect(center.x - cellSize / 2, center.y - cellSize, cellSize, cellSize);
            g2.drawRect(center.x - cellSize / 2, center.y, cellSize, cellSize);
            g2.drawRect(center.x - cellSize / 2, center.y + cellSize, cellSize, cellSize);
        }
    }

    /**
     * Position des Schiffes speichern.
     * 
     * @return 
     */
    private ArrayList<Point> getEndPoints() {
        ArrayList<Point> result = new ArrayList<>();
        int x = 0, xOffset = 0;
        int y = 0, yOffset = 0;
        switch (shipType) {
            case BATTLESHIP:
                if (isHorizontal) {
                    x = (center.x - cellSize * 3 / 2 - startX) / cellSize;
                    y = (center.y - startY) / cellSize;
                    xOffset = 3;
                } else {
                    y = (center.y - cellSize * 3 / 2 - startY) / cellSize;
                    x = (center.x - startX) / cellSize;
                    yOffset = 3;
                }
                break;
            case CRUISER:
                if (isHorizontal) {
                    x = (center.x - cellSize - startX) / cellSize;
                    y = (center.y - startY) / cellSize;
                    xOffset = 2;
                } else {
                    y = (center.y - cellSize - startY) / cellSize;
                    x = (center.x - startX) / cellSize;
                    yOffset = 2;
                }
                break;
            case DESTROYER:
                if (isHorizontal) {
                    x = (center.x - cellSize / 2 - startX) / cellSize;
                    y = (center.y - startY) / cellSize;
                    xOffset = 1;
                } else {
                    y = (center.y - cellSize / 2 - startY) / cellSize;
                    x = (center.x - startX) / cellSize;
                    yOffset = 1;
                }
                break;
            case UBOOT:
                x = (center.x - startX) / cellSize;
                y = (center.y - startY) / cellSize;
                break;
        }
        result.add(new Point(x, y));
        result.add(new Point(x + xOffset, y + yOffset));
        return result;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    private final Color valid = new Color(0.1f, 0.8f, 0.1f, 0.75f);
    private final Color invalid = new Color(1, 0.1f, 0.1f, 0.75f);
    private final Color border = new Color(0, 0, 0, 0.5f);
    private boolean isHorizontal = true;
    private ShipProperty shipType = NONE;
    private Point center;
    private Color shipColor = valid;
}
