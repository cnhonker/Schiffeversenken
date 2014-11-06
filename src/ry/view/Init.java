package ry.view;

import java.awt.Color;
import ry.Battleship;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JToggleButton;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import ry.network.Message;
import ry.models.GameModelState;
import ry.models.ShipProperty;
import ry.util.Utils;
import static ry.network.MessageProperty.USER_CANCELREADY;
import static ry.network.MessageProperty.USER_CLEAR;
import static ry.network.MessageProperty.USER_READY;
import static ry.network.MessageProperty.USER_RANDOM;
import static ry.network.MessageProperty.USER_UNDO;
import static ry.network.MessageProperty.USER_LOAD;
import static ry.network.MessageProperty.USER_SAVE;

/**
 * Platzieren der Schiffe
 * 
 * @author cnhonker
 */
public class Init extends BasicCard {

    /**
     *
     * @param owner
     */
    public Init(Battleship owner) {
        super(owner);
        initComponent();
        layoutComponents();
    }

    /**
     * Falls das Model sich geändert hat, wird der View über Änderungen
     * informiert Die Methode aktualisert den View an hand der Änderungen
     *
     * @param state
     */
    @Override
    public void update(GameModelState state) {
        boolean enemyReady = state.enemyReady();
        boolean userReady = state.userReady();

        if (userReady && !enemyReady) {
            ready.setText("Bereit, warte auf Gegner");
            save.setEnabled(false);
            load.setEnabled(false);
        } else if (!userReady && enemyReady) {
            ready.setText("Gegener ist bereit");
        }
        
        if (!userReady && !enemyReady) {
            ready.setText("Start!");
        }

        reset.setEnabled(!userReady);
        undo.setEnabled(!userReady && !state.hasLayoutShips());
        random.setEnabled(!userReady);

        fieldPanel.setFieldMatrix(state.getUserMatrix());
        fieldPanel.setColor(state.shipsReady());

        refreshStatus(state);

        if (state.isFieldRdy()) {
            JOptionPane.showMessageDialog(origin, "Es sind noch Schiffe übrig", "Information", JOptionPane.INFORMATION_MESSAGE);
            ready.setSelected(false);
        }
    }
    
    /**
     * Die Buttons anhand der Änderungen im Model aktualisieren
     * @param state 
     */
    private void refreshStatus(GameModelState state) {
        refreshStatusBattleShip(state);
        refreshStatusCruiser(state);
        refreshStatusDestroyer(state);
        refreshStatusSubmarine(state);
    }

    private void refreshStatusSubmarine(GameModelState state) {
        if (state.isUbootRdy()) {
            if (uShip.isEnabled()) {
                fieldPanel.setType(ShipProperty.NONE);
            }
            uShip.setSelected(false);
            uShip.setEnabled(false);
        } else {
            uShip.setEnabled(true);
        }
    }

    private void refreshStatusDestroyer(GameModelState state) {
        if (state.isDestroyerRdy()) {
            if (dShip.isEnabled()) {
                fieldPanel.setType(ShipProperty.NONE);
            }
            dShip.setSelected(false);
            dShip.setEnabled(false);
        } else {
            dShip.setEnabled(true);
        }
    }

    private void refreshStatusCruiser(GameModelState state) {
        if (state.isCruiserRdy()) {
            if (cShip.isEnabled()) {
                fieldPanel.setType(ShipProperty.NONE);
            }
            cShip.setSelected(false);
            cShip.setEnabled(false);
        } else {
            cShip.setEnabled(true);
        }
    }

    private void refreshStatusBattleShip(GameModelState state) {
        if (state.isBattleshipRdy()) {
            if (bShip.isEnabled()) {
                fieldPanel.setType(ShipProperty.NONE);
            }
            bShip.setSelected(false);
            bShip.setEnabled(false);
        } else {
            bShip.setEnabled(true);
        }
    }

    /**
     * Komponenten setzen
     */
    private void initComponent() {
        shipPanel = new JPanel(new GridBagLayout());
        shipPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLUE.brighter()), "Schiffe", TitledBorder.LEFT, TitledBorder.TOP));

        userPanel = new JPanel(new GridBagLayout());
        userPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLUE.brighter()), "Aktionen", TitledBorder.LEFT, TitledBorder.TOP));

        fieldPanel = new InitFeld(this, 10);

        initShipButton();
        initUserAction();
    }

    /**
     * Buttons für Schiffe
     */
    private void initShipButton() {
        bShip = new JToggleButton(battleShip());
        bShip.setHorizontalAlignment(SwingConstants.LEFT);
        bShip.setHorizontalTextPosition(SwingConstants.RIGHT);

        cShip = new JToggleButton(cruiser());
        cShip.setHorizontalAlignment(SwingConstants.LEFT);
        cShip.setHorizontalTextPosition(SwingConstants.RIGHT);

        dShip = new JToggleButton(destroyer());
        dShip.setHorizontalAlignment(SwingConstants.LEFT);
        dShip.setHorizontalTextPosition(SwingConstants.RIGHT);

        uShip = new JToggleButton(submarine());
        uShip.setHorizontalAlignment(SwingConstants.LEFT);
        uShip.setHorizontalTextPosition(SwingConstants.RIGHT);
    }

    /**
     * Button für User Aktionen
     */
    private void initUserAction() {
        reset = new JButton(reset());
        reset.setHorizontalAlignment(SwingConstants.LEFT);
        reset.setHorizontalTextPosition(SwingConstants.RIGHT);

        undo = new JButton(undo());
        undo.setHorizontalAlignment(SwingConstants.LEFT);
        undo.setHorizontalTextPosition(SwingConstants.RIGHT);

        ready = new JToggleButton(ready());
        ready.setHorizontalAlignment(SwingConstants.LEFT);
        ready.setHorizontalTextPosition(SwingConstants.RIGHT);

        random = new JButton(random());
        random.setHorizontalAlignment(SwingConstants.LEFT);
        random.setHorizontalTextPosition(SwingConstants.RIGHT);

        save = new JButton(save());
        save.setHorizontalAlignment(SwingConstants.LEFT);
        save.setHorizontalTextPosition(SwingConstants.RIGHT);

        load = new JButton(load());
        load.setHorizontalAlignment(SwingConstants.LEFT);
        load.setHorizontalTextPosition(SwingConstants.RIGHT);
    }

    /**
     * Komponente platzieren
     */
    private void layoutComponents() {
        layoutShipPanel();
        layoutUserPanel();
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.gridheight = 4;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.weightx = 1.0;
        add(fieldPanel, c);
        c.gridx = 4;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(shipPanel, c);
        c.gridx = 4;
        c.gridy = 1;
        c.weightx = 0;
        c.weighty = 1.0;
        add(userPanel, c);
    }

    /**
     * Schiffe platzieren
     */
    private void layoutShipPanel() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        shipPanel.add(bShip, c);
        c.gridy = 1;
        shipPanel.add(cShip, c);
        c.gridy = 2;
        shipPanel.add(dShip, c);
        c.gridy = 3;
        shipPanel.add(uShip, c);
    }

    /**
     * User Aktionen platzieren
     */
    private void layoutUserPanel() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        userPanel.add(random, c);
        c.gridy = 1;
        userPanel.add(undo, c);
        c.gridy = 2;
        userPanel.add(reset, c);
        c.gridy = 3;
        userPanel.add(ready, c);
        c.gridy = 4;
        userPanel.add(save, c);
        c.gridy = 5;
        userPanel.add(load, c);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        fieldPanel.setVisible(b);
        if (!b) {
            ready.setSelected(false);
            bShip.setEnabled(true);
            cShip.setEnabled(true);
            dShip.setEnabled(true);
            uShip.setEnabled(true);
        }
    }

    /**
     * Krigesschiff auf dem Spielfeld platzieren
     * @return AbstractAction für Button
     */
    private AbstractAction battleShip() {
        AbstractAction action = new AbstractAction("Kriegsschiff", Utils.getIcon("battleship.png")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (bShip.isSelected()) {
                    cShip.setSelected(false);
                    dShip.setSelected(false);
                    uShip.setSelected(false);

                    fieldPanel.setType(ShipProperty.BATTLESHIP);
                } else {
                    fieldPanel.setType(ShipProperty.NONE);
                }
            }

        };
        return action;
    }

    /**
     * Partouilleschiffe auf dem Spielfeld platzieren
     * @return AbstractAction für Button
     */
    private AbstractAction cruiser() {
        AbstractAction action = new AbstractAction("Patrouille", Utils.getIcon("cruiser.png")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cShip.isSelected()) {
                    bShip.setSelected(false);
                    dShip.setSelected(false);
                    uShip.setSelected(false);

                    fieldPanel.setType(ShipProperty.CRUISER);
                } else {
                    fieldPanel.setType(ShipProperty.NONE);
                }
            }

        };
        return action;
    }

    /**
     * Zerstörer auf dem Spielfeld platzieren
     * @return AbstractAction für Button
     */
    private AbstractAction destroyer() {
        AbstractAction action = new AbstractAction("Zertstörer", Utils.getIcon("destroyer.png")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (dShip.isSelected()) {
                    bShip.setSelected(false);
                    cShip.setSelected(false);
                    uShip.setSelected(false);

                    fieldPanel.setType(ShipProperty.DESTROYER);
                } else {
                    fieldPanel.setType(ShipProperty.NONE);
                }
            }
        };
        return action;
    }

    /**
     * UBoot auf dem Spielfeld platzieren
     * @return AbstractAction für Button
     */
    private AbstractAction submarine() {
        AbstractAction action = new AbstractAction("U-Boot", Utils.getIcon("submarine.png")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                origin.getAdapter().send(new Message(USER_RANDOM, null));
            }
        };
        return action;
    }
    
    /**
     * Schiffe durch Zufallsgenerator platzieren
     * @return AbstractAction für Button
     */
    private AbstractAction random() {
        AbstractAction action = new AbstractAction("Zufallsgenerator", Utils.getIcon("random.png")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                origin.getAdapter().send(new Message(USER_RANDOM, null));
            }
        };
        return action;
    }
    
    /**
     * Alles zurücksetzen
     * @return AbstractAction für Button
     */
    private AbstractAction reset() {
        AbstractAction action = new AbstractAction("Reset", Utils.getIcon("bin.png")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                origin.getAdapter().send(new Message(USER_CLEAR, null));
            }

        };
        return action;
    }

    /**
     * Rückgängig
     * @return AbstractAction für Button
     */
    private AbstractAction undo() {
        AbstractAction action = new AbstractAction("Rückgängig", Utils.getIcon("return.png")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                origin.getAdapter().send(new Message(USER_UNDO, null));
            }

        };
        return action;
    }

    /**
     * Bereit!
     * @return AbstractAction für Button 
     */
    private AbstractAction ready() {
        AbstractAction action = new AbstractAction("Bereit!", Utils.getIcon("rdy.png")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (ready.isSelected()) {
                    bling();
                    origin.getAdapter().send(new Message(USER_READY, null));
                } else {
                    origin.getAdapter().send(new Message(USER_CANCELREADY, null));
                }
            }

        };
        return action;
    }
    
    /**
     * Abspielen von Gameboy Start-Sound
     */
    public void bling() {
        try {
            final Clip clip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));

            clip.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                }
            });

            clip.open(AudioSystem.getAudioInputStream(getClass().getResourceAsStream("/wav/start.wav")));
            clip.start();
        } catch (Exception e) {
            // empty
        }
    }

    /**
     * Speichern! Diese Funktion steht sowohl dem Client als auch dem Server zur Verfügung!
     * @return AbstractAction für Button
     */
    private AbstractAction save() {
        AbstractAction action = new AbstractAction("Speichern", Utils.getIcon("save.png")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                origin.getAdapter().send(new Message(USER_SAVE, null));
            }
        };
        return action;
    }

    /**
     * Laden! Diese Funktion steht sowohl dem Client als auch dem Server zur Verfügung!
     * @return AbstractAction für Button
     */
    private AbstractAction load() {
        AbstractAction action = new AbstractAction("Laden", Utils.getIcon("load.png")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                origin.getAdapter().send(new Message(USER_LOAD, null));
            }

        };
        return action;
    }

    // Schiffe
    private JToggleButton bShip;
    private JToggleButton cShip;
    private JToggleButton dShip;
    private JToggleButton uShip;
    // User Action
    private JButton random;
    private JButton reset;
    private JButton undo;
    private JToggleButton ready;
    private JButton save;
    private JButton load;
    // Panels
    private JPanel shipPanel;
    private JPanel userPanel;
    private InitFeld fieldPanel;
}
