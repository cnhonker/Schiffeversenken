package ry;

import java.awt.AWTException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Robot;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.JLayer;
import javax.swing.SwingUtilities;
import ry.view.CommandAndControl;
import ry.models.GameModelState;
import ry.network.Message;
import ry.view.BasicCard;
import ry.view.Chat;
import ry.view.Ende;
import ry.view.Feld;
import ry.view.Init;
import ry.view.Main;
import ry.models.GameModel;
import ry.extras.PirateLayer;
import ry.util.Utils;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static ry.network.MessageProperty.USER_TERMINATE;

/**
 * View Owner
 * 
 * @author cnhonker
 */
public class Battleship extends JFrame implements PropertyChangeListener, CommandAndControl {

    private Robot bot;
    private final Map<String, BasicCard> views = new HashMap<>();
    private final JPanel cards = new JPanel(new CardLayout());
    private final GameModel mod;
    private JLayer<JPanel> pirateLayer;

    public Battleship(GameModel model) {
        mod = model;
        init();
        createCards();
        layoutComponents();
        pack();
        setLocationRelativeTo(null);
        mod.notifySubscribers();
    }

    /**
     * JFrame Grundoptionen setzen
     */
    private void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        setTitle(" Schiffe versenken - BWV Aachen");
        setIconImage(Utils.getIcon("anchor.png").getImage());
        setResizable(false);
        initListeners();
        try {
            bot = new Robot();
        } catch (AWTException ex) {
        }
    }
    
    /**
     * Konfiguriere Listeners für EventHandling
     */
    private void initListeners() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                send(new Message(USER_TERMINATE, null));                        //Informiert dem Verbindungspartner über das Schließen des Fensters
            }
        });
        mod.register(this);
    }

    /**
     * Das Model wird zurückgeliefert.
     *
     * @return GameModel
     */
    public GameModel getModel() {
        return mod;
    }

    /**
     * Erzeugen die verschiedenen Views
     */
    private void createCards() {
        Main main = new Main(this);
        PirateLayer praiteUI = new PirateLayer();
        pirateLayer = new JLayer<>(main, praiteUI);
        views.put("main", main);
        views.put("init", new Init(this));
        views.put("feld", new Feld(this));
        views.put("ende", new Ende(this));
        views.put("chat", new Chat(this));
        addViewToPanel();
    }

    /**
     * Füge die Views zum interne Panels hinzu.
     * Bei den 'Main' View wird der JLayer übergeben, damit die Animation
     * funktioniert.
     */
    private void addViewToPanel() {
        for (Map.Entry<String, BasicCard> entrySet : views.entrySet()) {
            String kenn = entrySet.getKey();
            if (kenn.equals("main")) {
                cards.add(kenn, pirateLayer);
            } else {
                cards.add(kenn, entrySet.getValue());
            }
        }
    }

    /**
     * Positionierung der Komponente mit GridBagLayout
     */
    private void layoutComponents() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 0.5;
        c.fill = GridBagConstraints.BOTH;
        getContentPane().add(cards, c);
        c.gridy = 1;
        c.weighty = 0.5;
        getContentPane().add(views.get("chat"), c);
    }

    /**
     * EventHandler für Status-Update. Wenn der Model durch Controller verändert
     * wird, wird der Ansicht entsprechend der Änderungen aktualisiert.
     * @param evt 
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        Object o = evt.getNewValue();
        if (propertyName.equals("StateChanged") && (Objects.nonNull(o) && (o instanceof GameModelState))) {
            update((GameModelState) o);
        }
    }

    /**
     * Speichert die Zustände der Frame als Bild
     */
    private void writeToDisk() {
        BufferedImage bImg = bot.createScreenCapture(this.getBounds());
        Path p = Paths.get(mod.getNetFolder());
        String typ = mod.isHost() ? "SERVER" : "CLIENT";
        try {
            ImageIO.write(bImg, "JPEG", p.resolve(typ + "_" + System.currentTimeMillis() + ".jpg").toFile());
        } catch (IOException ex) {
            
        }
    }

    /**
     * Damit wird der interne View Panel aktualisiert
     * 
     * @param state
     */
    public void update(GameModelState state) {
        CardLayout layout = (CardLayout) cards.getLayout();
        switch (state.getViewProp()) {
            case MAIN:
                layout.show(cards, "main");
                setPreferredSize(new Dimension(600, 450));
                views.get("chat").setVisible(false);
                views.get("main").update(state);
                ((Chat) views.get("chat")).clearChat();
                if (state.isDisconnected()) {
                    showMessageDialog(this, "Verbindung abgebrochen", "Information", INFORMATION_MESSAGE);
                }
                break;
            case INIT:
                layout.show(cards, "init");
                setPreferredSize(new Dimension(498, 518));
                views.get("chat").setVisible(true);
                views.get("init").update(state);
                views.get("chat").update(state);
                break;
            case FELD:
                layout.show(cards, "feld");
                setPreferredSize(new Dimension(628, 523));
                views.get("chat").setVisible(true);
                views.get("feld").update(state);
                views.get("chat").update(state);
                writeToDisk();
                break;
            case ENDE:
                layout.show(cards, "ende");
                views.get("chat").setVisible(true);
                views.get("ende").update(state);
                views.get("chat").update(state);
                break;
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                pack();
            }
        });
    }

    /**
     * Gibt den Views den Controller.
     *
     * @return
     */
    public CommandAndControl getAdapter() {
        return this;
    }

    /**
     * Delegiert eingehende Anweisungen an das Model
     *
     * @param msg
     */
    @Override
    public void send(Message msg) {
        mod.process(msg);
    }
}
