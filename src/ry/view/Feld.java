package ry.view;

import java.awt.Color;
import ry.Battleship;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import ry.network.Message;
import ry.models.GameModelState;
import ry.util.Utils;
import static ry.network.MessageProperty.USER_GAME_LOAD;
import static ry.network.MessageProperty.USER_GAME_SAVE;
import static ry.network.MessageProperty.USER_SURRENDERED;

/**
 *
 * @author cnhonker
 */
public class Feld extends BasicCard {
    
    /**
     *
     * @param anOwner
     */
    public Feld(Battleship anOwner) {
        super(anOwner);
        initComponent();
        layoutComponent();
    }

    /**
     * Falls Änderungen im Model gibt wird der View mit diesem Methode
     * aktualisiert
     * @param dump
     */
    @Override
    public void update(GameModelState dump) {
        userField.setFieldMatrix(dump.getUserMatrix());
        enemyField.setFieldMatrix(dump.getMatrixOfTheEnemy());
        info.setText(dump.isUserTurn() ? "Du bist dran!" : "Warte auf Gegener");
        aufgeben.setEnabled(dump.isUserTurn());
        save.setEnabled(dump.getRoll().equals("SERVER"));
        load.setEnabled(dump.getRoll().equals("SERVER"));
        repaint();
    }
    
    /**
     * Initialisierung der Komponente
     */
    private void initComponent() {
        userField = new PlaygroundUser(this, 10);
        enemyField = new PlaygroundEnemy(this, 10);

        info = new JLabel();
        info.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
        
        save = new JButton(save());
        save.setHorizontalAlignment(SwingConstants.LEFT);
        save.setHorizontalTextPosition(SwingConstants.RIGHT);
        
        load = new JButton(load());
        load.setHorizontalAlignment(SwingConstants.LEFT);
        load.setHorizontalTextPosition(SwingConstants.RIGHT);

        aufgeben = new JButton(surrender());
        aufgeben.setHorizontalAlignment(SwingConstants.LEFT);
        aufgeben.setHorizontalTextPosition(SwingConstants.RIGHT);
        
        header = new JPanel(new GridBagLayout());
    }
    
    /**
     * Das Spielaufgeben und den Gegener informieren dass man aufgegeben hat.
     * @return 
     */
    private AbstractAction surrender() {
        AbstractAction action = new AbstractAction("Aufgeben", Utils.getIcon("skull.png")) {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                origin.getAdapter().send(new Message(USER_SURRENDERED, null));
            }
        };
        return action;
    }
    
    /**
     * Spiel speichern und den Client Auffordern ebenfalls das Spiel zu speichern.
     * 
     * Die Methode steht nur den Server zur Verfügung
     * @return 
     */
    private AbstractAction save() {
        AbstractAction action = new AbstractAction("Speichern", Utils.getIcon("save.png")) {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                origin.getAdapter().send(new Message(USER_GAME_SAVE, null));
            }
        };
        return action;
    }
    
    /**
     * Spiel laden unn den Client aufforden ebenfalls einen Spielstand zu laden
     * 
     * Die Methode steht nur dem Server zur Verfügung.
     * @return 
     */
    private AbstractAction load() {
        AbstractAction action = new AbstractAction("Laden", Utils.getIcon("load.png")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                origin.getAdapter().send(new Message(USER_GAME_LOAD, null));
            }
            
        };
        return action;
    }
    
    /**
     * Platzierung der Komponenten im Header
     */
    private void layoutHeader() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(5, 5, 0, 0);
        header.add(info, c);
        c.gridx = 1;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LAST_LINE_END;
        header.add(save, c);
        c.gridx = 2;
        c.weightx = 0;
        header.add(load, c);
        c.gridx = 3;
        c.weightx = 0;
        c.insets = new Insets(5, 5, 0, 5);
        header.add(aufgeben, c);
    }
    
    /**
     * Platzierung der Komponenten
     */
    private void layoutComponent() {
        layoutHeader();
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridx = 2;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        add(header, c);
        
        Box fields = new Box(BoxLayout.X_AXIS);
        fields.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        fields.add(userField);
        fields.add(enemyField);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.weightx = 0.0;
        c.weighty = 0.5;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);
        add(fields, c);
    }

    private JLabel info;
    private JButton aufgeben;
    private JButton save;
    private JButton load;
    private JPanel header;
    private PlaygroundUser userField;
    private PlaygroundEnemy enemyField;
}
