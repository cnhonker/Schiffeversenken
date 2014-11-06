package ry.view;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import ry.network.Message;
import static ry.network.MessageProperty.USER_SHOT;

/**
 *
 * @author cnhonker
 */
public class PlaygroundEnemy extends PlaygroundUser {

    /**
     * Informiert den Gegener wo der Spieler angeklickt hat.
     * Die Oberklasse weisst wie die Figuren zu zeichnen sind.
     * 
     * @param parent
     * @param width
     */
    public PlaygroundEnemy(final BasicCard parent, int width) {
        super(parent, width);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    Point click = e.getPoint();
                    if (click.x >= startX && click.x <= endX && click.y >= startY && click.y <= endY) {
                        Point shot = new Point((click.x - startX - 5) / cellSize, (click.y - startY - 5) / cellSize);
                        parent.getOrigin().getAdapter().send(new Message(USER_SHOT, shot));
                    }
                }
            }
        });
    }
}
