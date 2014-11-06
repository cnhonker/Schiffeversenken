package ry.view;

import java.awt.Color;
import ry.Battleship;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import ry.network.Message;
import ry.models.GameModelState;
import ry.network.MessageProperty;
import ry.util.Utils;
import static ry.network.MessageProperty.USER_FINISHED;
import static ry.network.MessageProperty.USER_CONN;
import static ry.network.MessageProperty.USER_HOST;

/**
 * Hosten / Verbinden
 * 
 * @author cnhonker
 */
public class Main extends BasicCard {

    private static final ImageIcon BG = Utils.getIcon("Navy.jpg");

    /**
     *
     * @param owner
     */
    public Main(Battleship owner) {
        super(owner);
        initComponent();
        layoutComponents();
    }

    /**
     *
     * @param dump
     */
    @Override
    public void update(GameModelState dump) {
        if (dump.unableToConnect()) {
            JOptionPane.showMessageDialog(origin, "Verbindung fehlgeschlagen", "Error", JOptionPane.ERROR_MESSAGE);
            connecting = false;
            hosten.setEnabled(true);
            status.setText("");
        }
        if (dump.isHostNA()) {
            JOptionPane.showMessageDialog(origin, "Server nicht erreichbar!", "Error", JOptionPane.ERROR_MESSAGE);
            connecting = false;
            hosten.setEnabled(true);
            status.setText("");
        }
        status.setText("");
        connecting = false;
        hosting = false;
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (!b) {
            hosten.setText("Spiel hosten");
            hosten.setEnabled(true);
            connect.setEnabled(true);
            ipInput.setEnabled(true);
        }
    }

    /**
     * Komponente setzen
     */
    private void initComponent() {
        hosten = new JButton(hosten());
        hosten.setHorizontalAlignment(SwingConstants.LEFT);
        hosten.setHorizontalTextPosition(SwingConstants.RIGHT);
        //
        connect = new JButton(connect());
        connect.setHorizontalAlignment(SwingConstants.LEFT);
        connect.setHorizontalTextPosition(SwingConstants.RIGHT);
        //
        pathInfo = new JLabel("Common Path", Utils.getIcon("netfolder.png"), SwingConstants.LEFT);
        pathInfo.setPreferredSize(connect.getPreferredSize());
        pathInfo.setHorizontalTextPosition(SwingConstants.RIGHT);
        pathInfo.setHorizontalAlignment(SwingConstants.LEFT);
        pathInfo.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.BLACK));
        pathInfo.setOpaque(true);
        //
        status = new JLabel("");
        status.setOpaque(true);
        status.setPreferredSize(hosten.getPreferredSize());
        status.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        //
        ipInput = new JTextField("127.0.0.1");
        ipInput.setPreferredSize(connect.getPreferredSize());
        ipInput.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.BLACK));
        //
        pathInput = new JTextField("");
        pathInput.setPreferredSize(hosten.getPreferredSize());
        pathInput.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.BLACK));
    }

    /**
     * Komponente platzieren
     */
    private void layoutComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.SOUTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 1.0;
        add(hosten, c);
        c.anchor = GridBagConstraints.SOUTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0;
        c.weighty = 0;
        add(connect, c);
        c.anchor = GridBagConstraints.SOUTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        c.weightx = 0.5;
        add(status, c);
        c.gridx = 3;
        c.weightx = 0.0;
        c.gridwidth = 1;
        add(pathInfo, c);
        c.gridx = 4;
        c.weightx = 0.5;
        add(pathInput, c);
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 4;
        c.weighty = 0.0;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.SOUTHWEST;
        add(ipInput, c);
    }

    /**
     * Hosten Action 
     * @return AbstractAction für Button
     */
    private AbstractAction hosten() {
        AbstractAction action = new AbstractAction("Spiel hosten", Utils.getIcon("server.png")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (hosting) {
                    origin.getAdapter().send(new Message(USER_FINISHED, null));
                    hosting = false;
                    hosten.setText("Spiel hosten");
                    connect.setEnabled(true);
                    ipInput.setEnabled(true);
                    status.setText("");
                } else {
                    if (isNetworkPathAvailable()) {
                        origin.getAdapter().send(new Message(USER_HOST, null));
                        hosting = true;
                        hosten.setText("Abbrechen");
                        status.setText("Warte auf Spieler ...");
                        connect.setEnabled(false);
                        ipInput.setEnabled(false);
                    }
                }
            }
        };
        return action;
    }

    /**
     * Connect Action
     * @return AbstractAction für Button
     */
    private AbstractAction connect() {
        AbstractAction action = new AbstractAction("Verbinden", Utils.getIcon("client.png")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (connecting) {
                    return;
                }
                connecting = true;
                status.setVisible(true);
                status.setText("Verbinde, bitte warten ...");
                hosten.setEnabled(false);
                origin.getAdapter().send(new Message(USER_CONN, ipInput.getText()));
            }
        };
        return action;
    }

    /**
     * Erreichbarkeit des Netzwerks wird geprüft
     * @return 
     */
    private boolean isNetworkPathAvailable() {
        String txt = pathInput.getText().trim();
        if (txt.isEmpty()) {
            msg();
            return false;
        } else {
            File dir = new File(txt);
            if (dir.canWrite() && dir.isDirectory()) {
                origin.getAdapter().send(new Message(MessageProperty.NET_PATH, txt));
                return true;
            } else {
                msg();
                return false;
            }
        }
    }
    
    /**
     * Hintergrund zeichnen
     * @param g 
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(BG.getImage(), 0, 0, getWidth(), getHeight(), null);
    }
    
    /**
     * Warnmeldung ausgeben, falls der gemeinsame Pfad nicht erreichbar ist
     */
    private void msg() {
        JOptionPane.showMessageDialog(this, "Prüfen Sie den gemeinsamen Pfad!", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private JButton hosten;
    private JButton connect;
    private JTextField ipInput;
    private JTextField pathInput;
    private JLabel status;
    private JLabel pathInfo;
    private boolean connecting = false;
    private boolean hosting = false;
}
