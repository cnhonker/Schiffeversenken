package ry.view;

import java.awt.Color;
import ry.extras.Player;
import ry.Battleship;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import ry.network.Message;
import ry.models.GameModelState;
import ry.util.Utils;
import static ry.network.MessageProperty.USER_CANCELREADYNEXT;
import static ry.network.MessageProperty.USER_READYNEXT;
import static ry.network.MessageProperty.USER_FINISHED;

/**
 * Ende View
 * 
 * Wenn das Spiel zum Ende gespielt wurde oder wenn einer der beiden Spieler 
 * aufgegeben hat.
 * @author cnhonker
 */
public class Ende extends BasicCard {

    /**
     *
     * @param owner
     */
    public Ende(Battleship owner) {
        super(owner);
        initComponent();
        layoutComponent();
    }

    /**
     * Wenn der Panel nicht sichtbar ist dann soll der Button "Nochmal spielen 
     * ebenfalls deaktiviert sein.
     * @param b 
     */
    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (!b) {
            again.setSelected(false);
        }
    }

    /**
     * Falls es Änderungen im Model vorliegen, wird der View benachrichtigt und
     * entsprechende Änderungen werden anhand der Status vom Model vorgenommen.
     * @param dump
     */
    @Override
    public void update(GameModelState dump) {
        switch (dump.getGameProp()) {
            case WIN:
                result.setText("Sie haben gewonnen");
                break;
            case LOSS:
                result.setText("Sie haben verloren");
                break;
            case SURRENDER:
                result.setText("Sie haben aufgegeben");
                break;
            case SURRENDER_ENEMY:
                result.setText("Gegner hat aufgegeben!");
                break;
        }
        boolean isUserRdyAgain = dump.istNextRoundRdy();
        boolean isEnemyRdyAgain = dump.isEnemyNGRdy();

        if (isUserRdyAgain && !isEnemyRdyAgain) {
            again.setText("Nächstes Spiel starten");
        } else if (!isUserRdyAgain && isEnemyRdyAgain) {
            again.setText("Gegner bereit, Spiel starten!");
        } else if (!isUserRdyAgain && !isEnemyRdyAgain) {
            again.setText("Nochmal spielen");
        }

        userPanel.setFieldMatrix(dump.getUserMatrix());
        enemyPanel.setFieldMatrix(dump.getEnemyMatrix());
    }

    /**
     * Komponente initialisieren
     */
    private void initComponent() {
        result = new JLabel();
        result.setFont(new Font(Font.DIALOG, Font.BOLD, 18));

        toStart = new JButton(toStart());
        toStart.setHorizontalAlignment(SwingConstants.LEFT);
        toStart.setHorizontalTextPosition(SwingConstants.RIGHT);

        again = new JToggleButton(again());
        again.setHorizontalAlignment(SwingConstants.LEFT);
        again.setHorizontalTextPosition(SwingConstants.RIGHT);

        capture = new JButton(timeLapse());
        capture.setHorizontalAlignment(SwingConstants.LEFT);
        capture.setHorizontalTextPosition(SwingConstants.RIGHT);

        body = new JPanel(new GridBagLayout());
        body.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        userPanel = new PlaygroundUser(this, 10);
        enemyPanel = new PlaygroundUser(this, 10);
        userLabel = new JLabel("Dein Spielfeld");
        enemyLabel = new JLabel("Gegner's Spielfeld");
    }

    /**
     * Komponente platzieren
     */
    private void layoutComponent() {
        layoutBody();
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.insets = new Insets(20, 0, 5, 0);
        add(result, c);
        c.gridy = 1;
        c.gridwidth = 1;
        c.insets = new Insets(5, 5, 0, 5);
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.5;
        c.fill = GridBagConstraints.HORIZONTAL;
        add(toStart, c);
        c.gridx = 1;
        c.insets = new Insets(5, 5, 0, 5);
        add(again, c);
        c.gridx = 2;
        add(capture, c);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 3;
        c.insets = new Insets(5, 5, 5, 5);
        c.weighty = 0.5;
        c.fill = GridBagConstraints.BOTH;
        add(body, c);
    }

    /**
     * Das untere Panel wird den Matrizen werden platziert.
     */
    private void layoutBody() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 0.5;
        c.insets = new Insets(5, 0, 5, 0);
        body.add(userLabel, c);
        c.gridx = 1;
        body.add(enemyLabel, c);
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.5;
        c.fill = GridBagConstraints.BOTH;
        body.add(userPanel, c);
        c.gridx = 1;
        c.insets = new Insets(0, 0, 0, 0);
        body.add(enemyPanel, c);
    }

    /**
     * Funktionen für den Button "Zeitraffer"
     * @return 
     */
    private AbstractAction timeLapse() {
        AbstractAction action = new AbstractAction("Zeitraffer", Utils.getIcon("clock.png")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                Path dir = Paths.get(origin.getModel().getNetFolder());
                Player player = new Player(Arrays.asList(dir.toFile().listFiles()));
                player.showPanel();
            }
        };
        return action;
    }

    /**
     * Funktionen für den Button "Zurück zum Start"
     * @return 
     */
    private AbstractAction toStart() {
        AbstractAction action = new AbstractAction("Zurück zum Start", Utils.getIcon("back.png")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                origin.getAdapter().send(new Message(USER_FINISHED, null));
            }
        };
        return action;
    }

    /**
     * Funktionen für den Button "Nochmal spielen"
     * @return 
     */
    private AbstractAction again() {
        AbstractAction action = new AbstractAction("Nochmal spielen", Utils.getIcon("rdy.png")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (again.isSelected()) {
                    origin.getAdapter().send(new Message(USER_READYNEXT, null));
                } else {
                    origin.getAdapter().send(new Message(USER_CANCELREADYNEXT, null));
                }
            }
        };
        return action;
    }
    
    private JLabel result;
    private JLabel userLabel;
    private JLabel enemyLabel;
    private JButton toStart;
    private JButton capture;
    private JToggleButton again;
    private JPanel body;
    private PlaygroundUser userPanel;
    private PlaygroundUser enemyPanel;
}
