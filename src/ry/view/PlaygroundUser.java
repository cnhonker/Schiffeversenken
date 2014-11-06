package ry.view;

import javax.swing.JPanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import ry.models.Matrix;

/**
 * Das Spielfeld
 * 
 * @author cnhonker
 */
public class PlaygroundUser extends JPanel {

    /**
     *
     * @param view
     * @param width
     */
    public PlaygroundUser(BasicCard view, int width) {
        parent = view;
        matrixWidth = width;
        field = new Matrix(matrixWidth);
        calcSize();
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                calcSize();
                repaint();
            }

        });
    }

    /**
     * Berechnet den Anfang und Ende des Matrizen und Die Methode wird auch
     * aufgerufen wenn sich die Größe des Fensters geändert wurde
     */
    private void calcSize() {
        int minWidth = (getWidth() > getHeight()) ? getHeight() : getWidth();
        cellSize = (minWidth - 2 * 5) / matrixWidth;
        startX = getWidth() / 2 - (int) (cellSize * matrixWidth / 2.0);
        startY = getHeight() / 2 - (int) (cellSize * matrixWidth / 2.0);
        endX = getWidth() / 2 + (int) (cellSize * matrixWidth / 2.0);
        endY = getHeight() / 2 + (int) (cellSize * matrixWidth / 2.0);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        super.paintComponent(g);
        drawField(g);
    }

    @Override
    public void setVisible(boolean b) {
        if (!b) {
            field = new Matrix(matrixWidth);
        }
        super.setVisible(b);
    }

    public void setFieldMatrix(Matrix matrixField) {
        if (matrixField != null) {
            field = matrixField;
        }
        repaint();
    }

    /**
     * Kompakter Aufruf der beiden Methode: drawGrid und drawAllCells
     * @param g 
     */
    private void drawField(Graphics g) {
        drawGrid(g);
        drawAllCells(g);
    }

    /**
     * Das komplette Grundgerüst zeichnen
     * @param g 
     */
    private void drawGrid(Graphics g) {
        if (g == null) {
            return;
        }
        g.setColor(Color.BLACK);
        for (int i = 0; i <= matrixWidth; i++) {
            g.drawLine(startX, startY + i * cellSize, endX, startY + i * cellSize);
            g.drawLine(startX + i * cellSize, startY, startX + i * cellSize, endY);
        }
    }

    /**
     * Figuren (Schiffe, Kreuz, Punkte) zeichnen im Koordinatensystem
     *
     * @param g
     */
    private void drawAllCells(Graphics g) {
        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                drawCell(g, i, j);
            }
        }
    }

    /**
     * Anhand der Informationen im Field wird der richtige Zelltyp gewählt und
     * gezeichnet
     *
     * @param g
     * @param x
     * @param y
     */
    private void drawCell(Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;
        Stroke tempStroke = g2.getStroke();
        BasicStroke stroke = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2.setStroke(stroke);
        switch (field.getCellProp(x, y)) {
            case SHIP:
                g2.setPaint(Color.BLACK);
                g2.fillRect(startX + x * cellSize, startY + y * cellSize, cellSize, cellSize);
                g2.setPaint(Color.RED);
                g2.drawRect(startX + x * cellSize, startY + y * cellSize, cellSize, cellSize);
                break;
            case POINT:
                g2.setPaint(Color.GREEN);
                g2.fillOval(startX + (int) ((x + 0.5) * cellSize) - 1 / 2, startY + (int) ((y + 0.5) * cellSize) - 1 / 2, 4, 4);
                break;
            case DESTROYED:
                g2.setPaint(Color.MAGENTA);
                g2.fillRect(startX + x * cellSize, startY + y * cellSize, cellSize, cellSize);
                g2.setPaint(Color.RED);
                g2.drawRect(startX + x * cellSize, startY + y * cellSize, cellSize, cellSize);
                g2.drawLine(startX + (int) ((x + 0.25) * cellSize), startY + (int) ((y + 0.25) * cellSize), startX + (int) ((x + 0.75) * cellSize), startY + (int) ((y + 0.75) * cellSize));
                g2.drawLine(startX + (int) ((x + 0.25) * cellSize), startY + (int) ((y + 0.75) * cellSize), startX + (int) ((x + 0.75) * cellSize), startY + (int) ((y + 0.25) * cellSize));
                break;
        }
        g2.setStroke(tempStroke);
    }

    protected int cellSize;
    protected int startX, endX;
    protected int startY, endY;
    protected BasicCard parent;
    private final int matrixWidth;
    private Matrix field;
}
