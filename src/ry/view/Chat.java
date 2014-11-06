package ry.view;

import java.awt.Color;
import ry.Battleship;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import ry.network.Message;
import ry.models.GameModelState;
import static javax.swing.text.StyleConstants.setBold;
import static ry.network.MessageProperty.USER_CHAT;

/**
 * Chat View
 * 
 * @author cnhonker
 */
public class Chat extends BasicCard {

    public Chat(Battleship view) {
        super(view);
        setLayout(new GridBagLayout());
        initComponents();
        layoutComponents();
        setStyle();
    }

    /**
     * Wird vom Model ausgelöst, falls Änderungen vorliegen
     * 
     * Senden von Nachrichten
     * @param state
     */
    @Override
    public void update(GameModelState state) {
        String msg = state.getMessage();
        if (msg != null) {
            write(false, msg);
        }
    }

    /**
     * Löscht den Chatinhalt
     */
    public void clearChat() {
        Document doc = chat.getDocument();
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException e) {

        }
    }

    private void initComponents() {
        inputPanel = new JPanel(new GridBagLayout());
        chatLabel = new JLabel("Nachricht: ");
        initChatPane();
        initInputField();
        initSendButton();
    }

    /**
     * ChatPanel konfigurieren
     */
    private void initChatPane() {
        chat = new JTextPane();
        chat.setEditable(false);
        scroll = new JScrollPane(chat);
        scroll.getViewport().setPreferredSize(new Dimension(chat.getWidth(), 75));
    }

    /**
     * InputField konfigurieren
     */
    private void initInputField() {
        chatField = new JTextField();
        chatField.setAction(getSendAction());
        chatField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (chatField.getText().equals("")) {
                    senden.setEnabled(false);
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                if (!senden.isEnabled()) {
                    senden.setEnabled(true);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
    }

    /**
     * Senden Button konfigurieren
     */
    private void initSendButton() {
        senden = new JButton("Senden");
        senden.setEnabled(false);
        senden.setAction(getSendAction());
    }
    
    /**
     * Beim CR oder Drücken der Senden-Button wird diese Aktion ausgelöst
     * 
     * @return 
     */
    private AbstractAction getSendAction() {
        AbstractAction action = new AbstractAction("Senden") {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(chatField)) {
                    if (chatField.getText().isEmpty()) {
                        return;
                    }
                }
                String msg = chatField.getText();
                write(true, msg);
                origin.getAdapter().send(new Message(USER_CHAT, msg));
                senden.setEnabled(false);
                chatField.setText("");
            }
        };
        return action;
    }

    /**
     * Komponenten platzieren
     */
    private void layoutComponents() {
        layoutInputPanel();
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(0, 0, 5, 0);
        add(scroll, c);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0, 0, 0, 0);
        add(inputPanel, c);
    }
    
    /**
     * Kleiner Trick um den Hauptpanel 'auszufüllen'
     */
    private void layoutInputPanel() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.BOTH;
        inputPanel.add(chatLabel, c);
        c.gridx = 1;
        c.gridwidth = 2;
        c.weightx = 1.0;
        inputPanel.add(chatField, c);
        c.gridx = 3;
        c.gridwidth = 1;
        c.weightx = 0;
        inputPanel.add(senden, c);
    }

    /**
     * Attribute für MSG
     */
    private void setStyle() {
        setPlayerStyle();
        setEnemyStyle();
    }
    
    /**
     * Sytle von User
     */
    private void setPlayerStyle() {
        player = chat.addStyle("ich", null);
        StyleConstants.setForeground(player, Color.BLUE);
        setBold(player, true);
    }
    
    /**
     * Syle von Gegener
     */
    private void setEnemyStyle() {
        enemy = chat.addStyle("gegner", null);
        StyleConstants.setForeground(enemy, Color.RED);
        setBold(enemy, true);
    }

    private void write(boolean isUser, String msg) {
        StyledDocument doc = chat.getStyledDocument();
        try {
            if (isUser) {
                doc.insertString(doc.getLength(), "Ich", player);
            } else {
                doc.insertString(doc.getLength(), "Gegner", enemy);
            }
            doc.insertString(doc.getLength(), String.format(": %s%n", msg), null);
        } catch (BadLocationException e) {
            // empty
        }
        chat.setCaretPosition(doc.getLength());
    }

    private JScrollPane scroll;
    private JTextPane chat;
    private JTextField chatField;
    private JButton senden;
    private JLabel chatLabel;
    private Style player;
    private Style enemy;
    private JPanel inputPanel;
}
