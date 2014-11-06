package ry.network;

import ry.models.GameModel;

/**
 * Grundgerüst
 * 
 * @author cnhonker
 */
public abstract class BasicNetwork implements Runnable {

    /**
     *
     * @param gameModel
     */
    public BasicNetwork(GameModel gameModel) {
        model = gameModel;
    }

    public abstract void establish();

    public abstract void terminate();

    public abstract void sendMsg(NetMessage msg);

    protected abstract void recieveMsg();

    protected final int port = 55555;

    protected final GameModel model;
}
