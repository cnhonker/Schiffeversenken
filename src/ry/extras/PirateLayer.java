package ry.extras;

import java.awt.AWTEvent;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.plaf.LayerUI;

/**
 * JLayer mit dem Piratelogo
 * 
 * @author ry
 */
public class PirateLayer extends LayerUI<JPanel> {

    private Image img;

    /**
     * Mightypirate zeichnen
     * @param g
     * @param c 
     */
    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
        int x = c.getX() + c.getWidth() / 2 - img.getWidth(c)/ 2;
        int y = c.getY() + c.getHeight() / 2 - img.getHeight(c) / 2;
        if (img != null) {
            g.drawImage(img, x, y, c);
        }
    }

    /**
     * Die Methode wird intern beim setzen der UI aufgerufen
     * @param c 
     */
    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        JLayer jlayer = (JLayer) c;
        jlayer.setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        img = new ImageIcon(PirateLayer.class.getResource("/img/pirate.gif")).getImage();
    }

    /**
     * Aufräumearbeit wenn der UI nicht mehr benötigt wird.
     * @param c 
     */
    @Override
    public void uninstallUI(JComponent c) {
        JLayer jlayer = (JLayer) c;
        jlayer.setLayerEventMask(0);
        super.uninstallUI(c);
    }
}
