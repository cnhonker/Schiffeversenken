package ry.extras;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Der Spielverlauf im Zeitraffer anzeigen
 *
 * @author ry
 */
public class Player extends JPanel implements ChangeListener {

    private final List<File> images;
    private List<File> serverImages;
    private List<File> clientImages;
    private JSlider slider;
    private JSplitPane splitPane;
    private JDialog diag;
    private JLabel serverLabel;
    private JLabel clientLabel;

    /**
     *
     * @param list Dateien im angegebenen Pfad 'Common Path'
     */
    public Player(List<File> list) {
        images = list;
        splitList();
        initComponents();
        layoutComponents();
    }
    
    /**
     * Füge Panel Komponente hinzu
     */
    private void layoutComponents() {
        setLayout(new BorderLayout());
        add(splitPane, BorderLayout.CENTER);
        add(slider, BorderLayout.SOUTH);
        diag.add(this);
    }

    /**
     * Die Methode teilt die Liste in Server und Client Images auf
     */
    private void splitList() {
        serverImages = new ArrayList<>();
        clientImages = new ArrayList<>();
        for (File f : images) {
            String name = f.getName();

            if (name.startsWith("SERVER")) {
                serverImages.add(f);
            }
            if (name.startsWith("CLIENT")) {
                clientImages.add(f);
            }
        }
    }

    /**
     * Komponente initialisieren
     */
    private void initComponents() {
        initDialog();
        initSlider();
        initServer();
        initClient();
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false, serverLabel, clientLabel);
    }

    /**
     * JDialog configurieren
     */
    private void initDialog() {
        diag = new JDialog();
        diag.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        diag.setTitle("Zeitraffer");
        diag.setIconImage(new ImageIcon(Player.class.getResource("/img/tpb.png")).getImage());
    }

    /**
     * Slider configurieren
     */
    private void initSlider() {
        int max = Math.max(serverImages.size(), clientImages.size());
        slider = new JSlider(JSlider.HORIZONTAL, 0, max, 0);
        slider.setLabelTable(slider.createStandardLabels(1, max));
        slider.setPaintLabels(true);
        slider.addChangeListener(this);
    }

    /**
     * Server Label configurieren
     */
    private void initServer() {
        serverLabel = new JLabel("");
        serverLabel.setPreferredSize(new Dimension(628, 523));
        serverLabel.setHorizontalTextPosition(JLabel.CENTER);
        serverLabel.setHorizontalAlignment(JLabel.CENTER);
        serverLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("SERVER"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        if (serverImages.size() > 0) {
            serverLabel.setIcon(new ImageIcon(serverImages.get(0).toString()));
        }
    }

    /**
     * Client Label configurieren
     */
    private void initClient() {
        clientLabel = new JLabel("");
        clientLabel.setPreferredSize(new Dimension(628, 523));
        clientLabel.setHorizontalTextPosition(JLabel.CENTER);
        clientLabel.setHorizontalAlignment(JLabel.CENTER);
        clientLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("CLIENT"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        if (clientImages.size() > 0) {
            clientLabel.setIcon(new ImageIcon(clientImages.get(0).toString()));
        }
    }

    /**
     * Changelistener wenn der Benutzer den Slider verschiebt
     *
     * @param e
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            int v = slider.getValue();
            setServerLabel(v);
            setClientLabel(v);
        }
    }

    /**
     * Bild für Server setzen
     *
     * @param i
     */
    private void setServerLabel(int i) {
        if (i < serverImages.size()) {
            serverLabel.setText("");
            serverLabel.setIcon(new ImageIcon(serverImages.get(i).toString()));
        } else {
            serverLabel.setIcon(null);
            serverLabel.setText("No Images Available");
        }
    }

    /**
     * Bild für Client setzen
     *
     * @param i
     */
    private void setClientLabel(int i) {
        if (i < clientImages.size()) {
            clientLabel.setText("");
            clientLabel.setIcon(new ImageIcon(clientImages.get(i).toString()));
        } else {
            clientLabel.setIcon(null);
            clientLabel.setText("No Images Available");
        }
    }

    /**
     * Zeigt den Panel
     */
    public void showPanel() {
        diag.pack();
        diag.setLocationRelativeTo(null);
        diag.setVisible(true);
    }
}
