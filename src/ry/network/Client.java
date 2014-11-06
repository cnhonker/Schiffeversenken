package ry.network;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import ry.models.GameModel;
import static ry.network.MessageProperty.NET_FAIL;
import static ry.network.MessageProperty.NET_BYE;
import static ry.network.MessageProperty.NET_ERROR;
import static ry.network.MessageProperty.NET_UNKNOWN;
import static ry.network.MessageProperty.SWITCH_INIT;

/**
 * GameModel verarbeitet ein- und aussgehende Nachrichten und Behandlung von Exceptions
 * @see http://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
 * @author cnhonker
 */
public class Client extends BasicNetwork {

    public Client(GameModel model) {
        super(model);
    }

    @Override
    public void run() {
        establish();
        while (socket != null && !socket.isClosed()) {
            recieveMsg();
        }
    }

    @Override
    public void establish() {
        try {
            socket = new Socket(host, port);
            os = new ObjectOutputStream(socket.getOutputStream());
            is = new ObjectInputStream(socket.getInputStream());
            model.process(new Message(SWITCH_INIT, null));
        } catch (UnknownHostException e) {
            model.process(new Message(NET_UNKNOWN, null));
        } catch (SocketException e) {
            model.process(new Message(NET_FAIL, null));
        } catch (IOException e) {
            model.process(new Message(NET_ERROR, e.toString()));
        }
    }

    @Override
    public void terminate() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            // empty
        }
    }

    @Override
    public void sendMsg(NetMessage msg) {
        try {
            os.writeObject(msg);
            os.flush();
        } catch (IOException e) {
            // empty
        }
    }

    public void setHostAddress(String adresse) {
        host = adresse;
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
            } else if (e instanceof SocketException && e.getMessage().toLowerCase().equals("socket closed")) {
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

    private Socket socket;
    private String host;
    private ObjectInputStream is;
    private ObjectOutputStream os;
}
