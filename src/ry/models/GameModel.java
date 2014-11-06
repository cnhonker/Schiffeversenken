package ry.models;

import ry.network.Client;
import ry.network.Host;
import ry.network.BasicNetwork;
import ry.network.Message;
import ry.network.NetMessage;
import ry.view.CardProperty;
import java.awt.Point;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.lang.System.exit;
import static ry.models.CellProperty.DEFAULT;
import static ry.models.CellProperty.DESTROYED;
import static ry.models.CellProperty.SHIP;
import static ry.models.CellProperty.POINT;
import static ry.network.MessageProperty.NET_MSG;
import static ry.network.MessageProperty.NET_BYE;
import static ry.network.MessageProperty.NET_RDY;
import static ry.network.MessageProperty.NET_RDYNEXTGAME;
import static ry.network.MessageProperty.NET_NOTRDY;
import static ry.network.MessageProperty.NET_NOTRDYNEXTGAME;
import static ry.network.MessageProperty.NET_REQUEST;
import static ry.network.MessageProperty.NET_RESPONSE;
import static ry.network.MessageProperty.NET_SURRENDERED;
import static ry.network.MessageProperty.NET_SHOTP;
import static ry.models.ResultProperty.LOSS;
import static ry.models.ResultProperty.SURRENDER_ENEMY;
import static ry.models.ResultProperty.SURRENDER;
import static ry.models.ResultProperty.WIN;
import static ry.view.CardProperty.INIT;
import static ry.view.CardProperty.ENDE;
import static ry.view.CardProperty.FELD;
import static ry.view.CardProperty.MAIN;
import static ry.models.ShipProperty.BATTLESHIP;
import static ry.models.ShipProperty.CRUISER;
import static ry.models.ShipProperty.DESTROYER;
import static ry.models.ShipProperty.UBOOT;
import static ry.network.MessageProperty.LOAD_REQ;
import static ry.network.MessageProperty.NET_PATH;
import static ry.network.MessageProperty.NET_PATH_REQ;
import static ry.network.MessageProperty.SAVE_REQ;

/**
 * Das GameModel
 *
 * @author cnhonker
 */
public class GameModel {

    /**
     * Es gibt verschiede Variante Observer Pattern zu implementieren.
     */
    private final PropertyChangeSupport change;
    
    public void register(PropertyChangeListener pcl) {
        change.addPropertyChangeListener(pcl);
    }

    public void remove(PropertyChangeListener pc1) {
        change.removePropertyChangeListener(pc1);
    }

    public void notifySubscribers() {
        GameModelState activeState = getModelProp();
        change.firePropertyChange("StateChanged", null, activeState);
    }

    public GameModel() {
        currentView = MAIN;
        change = new PropertyChangeSupport(this);
    }
    
    public boolean isHost() {
        return isHost;
    }

    /**
     * Ein- und ausgehende Nachrichten verarbeiten
     *
     * @param msg
     */
    public void process(Message msg) {
        switch (msg.getMessageProp()) {
            case SWITCH_MAIN:
                reset();
                break;
            case SWITCH_INIT:
                currentView = INIT;
                ally = new ShipOutline(10);
                enemyConnected = true;
                break;
            case SWITCH_FELD:
                currentView = FELD;
                isUserRdy = isHost;
                break;
            case SWITCH_END:
                currentView = ENDE;
                break;
            case NET_SHOTP:
                Point shot = (Point) msg.getPayload();
                CellProperty oldCell = ally.getCellProp(shot.x, shot.y);
                CellProperty newCell = DEFAULT;
                if (oldCell == DEFAULT) {
                    newCell = POINT;
                    isUserRdy = !isUserRdy;
                } else if (oldCell == SHIP) {
                    newCell = DESTROYED;
                }

                ally.setCellProp(newCell, shot.x, shot.y);
                if (ally.shipsCleared()) {
                    currentView = ENDE;
                    gameState = LOSS;
                }
                break;
            case NET_REQUEST:
                net.sendMsg(new NetMessage(NET_RESPONSE, ally));
                break;
            case NET_RESPONSE:
                if (enemy == null) {
                    net.sendMsg(new NetMessage(NET_RESPONSE, ally));
                }
                enemy = (ShipOutline) msg.getPayload();
                currentView = FELD;
                isUserRdy = isHost;
                break;
            case NET_RDY:
                isOpponentRdy = true;
                if (isRdyforTurn) {
                    net.sendMsg(new NetMessage(NET_REQUEST, null));
                }
                break;
            case NET_NOTRDY:
                isOpponentRdy = false;
                break;
            case NET_RDYNEXTGAME:
                isEnemyRdyforNextGame = true;
                if (isNextRoundRdy) {
                    terminateGame();
                    currentView = INIT;
                    ally = new ShipOutline(10);
                }
                break;
            case NET_NOTRDYNEXTGAME:
                isEnemyRdyforNextGame = false;
                break;
            case NET_BYE:
                enemyDiscon = true;
                reset();
                break;
            case NET_FAIL:
                connectError = true;
                terminateNetwork();
                break;
            case NET_UNKNOWN:
                unknownHost = true;
                terminateNetwork();
                break;
            case NET_SURRENDERED:
                currentView = ENDE;
                gameState = SURRENDER_ENEMY;
                break;
            case NET_MSG:
                lastMsg = (String) msg.getPayload();
                break;
            case NET_ERROR:
                unknownErr = (String) msg.getPayload();
                break;
            case USER_SHOT:
                if (isUserRdy) {
                    Point shotp = (Point) msg.getPayload();
                    CellProperty shotCell = enemy.getCellProp(shotp.x, shotp.y);
                    if (shotCell == DEFAULT || shotCell == SHIP) {
                        net.sendMsg(new NetMessage(NET_SHOTP, (Point) msg.getPayload()));
                        if (shotCell == DEFAULT) {
                            enemy.setCellProp(POINT, shotp.x, shotp.y);
                            isUserRdy = !isUserRdy;
                        } else {
                            enemy.setCellProp(DESTROYED, shotp.x, shotp.y);
                        }
                        if (enemy.shipsCleared()) {
                            currentView = ENDE;
                            gameState = WIN;
                        }
                    }
                }
                break;
            case USER_READY:
                if (!ally.allplaced()) {
                    allShipPlaced = true;
                } else {
                    isRdyforTurn = true;
                    if (isOpponentRdy) {
                        net.sendMsg(new NetMessage(NET_REQUEST, null));
                    } else {
                        net.sendMsg(new NetMessage(NET_RDY, null));
                    }
                }
                break;
            case USER_CANCELREADY:
                isRdyforTurn = false;
                net.sendMsg(new NetMessage(NET_NOTRDY, null));
                break;
            case USER_FINISHED:
                reset();
                break;
            case USER_READYNEXT:
                isNextRoundRdy = true;
                if (!isEnemyRdyforNextGame) {
                    net.sendMsg(new NetMessage(NET_RDYNEXTGAME, null));
                } else {
                    net.sendMsg(new NetMessage(NET_RDYNEXTGAME, null));
                    terminateGame();
                    currentView = INIT;
                    ally = new ShipOutline(10);
                }
                break;
            case USER_CANCELREADYNEXT:
                isNextRoundRdy = false;
                net.sendMsg(new NetMessage(NET_NOTRDYNEXTGAME, null));
                break;
            case USER_SURRENDERED:
                currentView = ENDE;
                gameState = SURRENDER;
                net.sendMsg(new NetMessage(NET_SURRENDERED, null));
                break;
            case USER_SHIPPLACED:
                Object[] shipCreationRequest = (Object[]) msg.getPayload();
                Ship newShip = new Ship((ShipProperty) shipCreationRequest[0], (Point) shipCreationRequest[1], (Point) shipCreationRequest[2]);
                ally.addShipToList(newShip);
                if (!ally.valid()) {
                    ally.removeLastShip();
                }
                break;
            case USERACTION_SHIPMOVED:
                Object[] shipMovingRequest = (Object[]) msg.getPayload();
                Ship movedShip = new Ship((ShipProperty) shipMovingRequest[0], (Point) shipMovingRequest[1], (Point) shipMovingRequest[2]);
                ally.addShipToList(movedShip);
                validShipPlacement = ally.valid();
                ally.removeLastShip();
                break;
            case USER_CLEAR:
                ally.clear();
                randomPlaced = false;
                break;
            case USER_UNDO:
                if (randomPlaced) {
                    ally.clear();
                    randomPlaced = false;
                } else {
                    ally.removeLastShip();
                }
                break;
            case USER_RANDOM:
                ally.random();
                randomPlaced = true;
                break;
            case USER_TERMINATE:
                if (net != null) {
                    if (enemyConnected) {
                        net.sendMsg(new NetMessage(NET_BYE, null));
                    }
                    net.terminate();
                }
                exit(0);
                break;
            case USER_CHAT:
                net.sendMsg(new NetMessage(NET_MSG, (String) msg.getPayload()));
                break;
            case USER_HOST:
                net = new Host(this);
                (new Thread(net)).start();
                isHost = true;
                break;
            case USER_CONN:
                net = new Client(this);
                ((Client) net).setHostAddress((String) (msg.getPayload()));
                netThread = new Thread(net);
                netThread.start();
                isHost = false;
                break;
            case USER_SAVE:
                save();

                break;
            case USER_LOAD:
                load();
                break;
            case USER_GAME_SAVE:
                saveGame();
                net.sendMsg(new NetMessage(SAVE_REQ, null));
                break;
            case USER_GAME_LOAD:
                loadGame();
                net.sendMsg(new NetMessage(LOAD_REQ, null));
                break;
            case SAVE_REQ:
                saveGame();
                break;
            case LOAD_REQ:
                loadGame();
                break;
            case NET_PATH:
                netFolder = (String) (msg.getPayload());
                break;
            case NET_PATH_REQ:
                net.sendMsg(new NetMessage(NET_PATH, netFolder));
                break;

        }
        notifySubscribers();
    }

    /**
     * Platzierung der Schiffe speichern 
     */
    private void save() {
        Path initPath = Paths.get(netFolder).resolve(isHost ? "host_init.dat" : "client_init.dat");
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(initPath.toFile()));
            if (ally != null) {
                os.writeObject(ally);
                os.close();
            }
        } catch (FileNotFoundException ex) {
            // empty
        } catch (IOException ex) {
            // empty
        }
    }

    /**
     * Platzierung der Schiffe laden
     */
    private void load() {
        Path initPath = Paths.get(netFolder).resolve(isHost ? "host_init.dat" : "client_init.dat");
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(initPath.toFile()));
            Object o = is.readObject();
            if (o instanceof ShipOutline) {
                ally = (ShipOutline) o;
            }
        } catch (FileNotFoundException ex) {
            // empty
        } catch (IOException ex) {
            // empty
        } catch (ClassNotFoundException ex) {
            // empty
        }
    }

    /**
     * Spiel speichern
     */
    private void saveGame() {
        Path allyPath = Paths.get(netFolder).resolve(isHost ? "host_ally.dat" : "client_ally.dat");
        Path enemyPath = Paths.get(netFolder).resolve(isHost ? "host_enemy.dat" : "client_enemy.dat");
        try {
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(allyPath.toFile()));
            os.writeObject(ally);
            os.close();
            os = new ObjectOutputStream(new FileOutputStream(enemyPath.toFile()));
            os.writeObject(enemy);
            os.close();
        } catch (FileNotFoundException ex) {
            // empty
        } catch (IOException ex) {
            // empty
        }
    }

    /**
     * Spiel laden
     */
    private void loadGame() {
        Path allyPath = Paths.get(netFolder).resolve(isHost ? "host_ally.dat" : "client_ally.dat");
        Path enemyPath = Paths.get(netFolder).resolve(isHost ? "host_enemy.dat" : "client_enemy.dat");

        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(allyPath.toFile()));
            ally = (ShipOutline) is.readObject();
            is.close();
            is = new ObjectInputStream(new FileInputStream(enemyPath.toFile()));
            enemy = (ShipOutline) is.readObject();
            is.close();
        } catch (FileNotFoundException ex) {
            // empty
        } catch (IOException ex) {
            // empty
        } catch (ClassNotFoundException ex) {
            // empty
        }
    }

    public String getNetFolder() {
        return netFolder;
    }

    /**
     * Alles zurücksetzen
     */
    private void reset() {
        currentView = MAIN;
        terminateNetwork();
        terminateGame();
    }

    private void terminateNetwork() {
        if (net != null) {
            net.terminate();
        }
        net = null;
        netThread = null;
        enemyConnected = false;
    }

    private void terminateGame() {
        ally = null;
        enemy = null;
        isOpponentRdy = false;
        isRdyforTurn = false;
        isNextRoundRdy = false;
        isEnemyRdyforNextGame = false;
        gameState = null;
    }

    private GameModelState getModelProp() {
        GameModelState prop = new GameModelState();
        prop.setView(currentView);
        if (ally != null) {
            prop.setMyMatrix(ally.getMatrix());
            prop.setBattleshipRdy(ally.userReady(BATTLESHIP));
            prop.setCruiserRdy(ally.userReady(CRUISER));
            prop.setDestroyerRdy(ally.userReady(DESTROYER));
            prop.setUbootRdy(ally.userReady(UBOOT));
            prop.setLayoutShips(ally.status());
        }
        if (enemy != null) {
            prop.setMatrixOfTheEnemy(enemy.getUnderLineMatrix());
            prop.setCurrentMatrixEnemy(enemy.getMatrix());
        }
        prop.setUserTurn(isUserRdy);
        prop.setReady(isRdyforTurn);
        prop.setEnemyRdy(isOpponentRdy);
        prop.setNextRoundRdy(isNextRoundRdy);
        prop.setEnemyNGRdy(isEnemyRdyforNextGame);
        prop.setFieldRdy(allShipPlaced);
        allShipPlaced = false;
        prop.setShipsRdy(validShipPlacement);
        prop.setEnemyNetStatus(enemyDiscon);
        enemyDiscon = false;
        prop.setNetNAStatus(connectError);
        connectError = false;
        prop.setHostStatus(unknownHost);
        unknownHost = false;
        if (lastMsg != null) {
            prop.setMessage(lastMsg);
            lastMsg = null;
        }
        if (unknownErr != null) {
            prop.setUnknownErr(unknownErr);
            unknownErr = null;
        }
        prop.setGameProp(gameState);
        prop.setRoll(isHost);
        return prop;
    }

    private String netFolder = "";
    private CardProperty currentView;
    private BasicNetwork net;
    private Thread netThread;
    private ShipOutline ally;
    private ShipOutline enemy;
    private ResultProperty gameState;
    private String lastMsg;
    private String unknownErr;
    private boolean enemyDiscon = false;
    private boolean unknownHost = false;
    private boolean connectError = false;
    private boolean isRdyforTurn = false;
    private boolean enemyConnected = false;
    private boolean isHost;
    private boolean isUserRdy;
    private boolean allShipPlaced = false;
    private boolean randomPlaced = false;
    private boolean isOpponentRdy = false;
    private boolean isEnemyRdyforNextGame = false;
    private boolean isNextRoundRdy = false;
    private boolean validShipPlacement = true;
}
