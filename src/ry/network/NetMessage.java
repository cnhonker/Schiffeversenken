package ry.network;

import java.io.Serializable;

/**
 * Versenden von Nachrichten mit serialisierbare Objekte übers Netzwerk.
 * @author cnhonker
 */
public class NetMessage implements Serializable {
    
    /**
     *
     * @param props
     * @param payload
     */
    public NetMessage(MessageProperty props, Serializable payload) {
        prop = props;
        pl = payload;
    }
    
    /**
     *
     * @return
     */
    public MessageProperty getMsgProp() {
        return prop;
    }
    
    /**
     *
     * @return
     */
    public Serializable getMsgPayload() {
        return pl;
    }
    
    private final MessageProperty prop;
    private final Serializable pl;
}