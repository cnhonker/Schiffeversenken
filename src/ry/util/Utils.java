package ry.util;

import javax.swing.ImageIcon;

/**
 * Auxilary-Klasse
 * 
 * @author ry
 */
public class Utils {

    /**
     * Holt die Bilder aus package /img/
     * @param name
     * @return 
     */
    public static ImageIcon getIcon(String name) {
        return new ImageIcon(Utils.class.getClass().getResource("/img/" + name));
    }
}
