package ry.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import ry.models.GameModel;
import static ry.network.MessageProperty.NET_BYE;
import static ry.network.MessageProperty.NET_ERROR;
import static ry.network.MessageProperty.NET_PATH_REQ;
import static ry.network.MessageProperty.SWITCH_INIT;

/**
 * GameModel verarbeitet ein- und aussgehende Nachrichten und Behandlung von Exceptions
 * @see http://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
 * @author cnhonker
 */
public class Host extends BasicNetwork {

    public Host(GameModel model) {
        super(model);
    }

    @Override
    public void run() {
        establish();
        while (client != null && !client.isClosed()) {
            recieveMsg();
        }
    }

    @Override
    public void establish() {
        try {
            socket = new ServerSocket(port);
            client = socket.accept();
            os = new ObjectOutputStream(client.getOutputStream());
            is = new ObjectInputStream(client.getInputStream());
            model.process(new Message(SWITCH_INIT, null));
            model.process(new Message(NET_PATH_REQ, null));
        } catch (SocketException e) {
            // empty
        } catch (IOException e) {
            model.process(new Message(NET_ERROR, e.toString()));
        }
    }

    @Override
    public void sendMsg(NetMessage aMessage) {
        try {
            os.writeObject(aMessage);
            os.flush();
        } catch (IOException e) {
            // empty
        }
    }

    @Override
    protected void recieveMsg() {
        NetMessage result = null;
        try {
            result = (NetMessage) is.readObject();
        } catch (ClassNotFoundException e) {
            // empty
        } catch (IOException e) {
            if (e instanceof EOFException) {
                model.process(new Message(NET_BYE, null));
            }
            else if (e instanceof SocketException && e.getMessage().toLowerCase().equals("socket closed")) {
            } else if (e instanceof SocketException && e.getMessage().equals("Connection reset")) {
                model.process(new Message(NET_BYE, null));
            } else {
                // empty
            }
        }
        if (result != null) {
            model.process(new Message(result.getMsgProp(), result.getMsgPayload()));
        }
    }

    /**
     *
     */
    @Override
    public void terminate() {
        try {
            if (client != null && !client.isClosed()) {
                client.close();
            }
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ServerSocket socket;
    private Socket client;
    private ObjectInputStream is;
    private ObjectOutputStream os;
}
