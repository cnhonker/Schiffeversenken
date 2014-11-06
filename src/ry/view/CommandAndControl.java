package ry.view;

import ry.network.Message;

/**
 * Das Controllerinterface
 * 
 * @author cnhonker
 */
public interface CommandAndControl {

    /**
     *
     * @param msg
     */
    void send(Message msg);
}
