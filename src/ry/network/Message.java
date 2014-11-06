package ry.network;

/**
 * Lokale Nachrichten mit Objekten
 * @author cnhonker
 */
public class Message {

    /**
     * Eine lokale Nachrichte
     * @param props - Nachrichtentyp
     * @param payload - Objekte
     */
    public Message(MessageProperty props, Object payload) {
        prop = props;
        pay = payload;
    }

    /**
     *
     * @return
     */
    public MessageProperty getMessageProp() {
        return prop;
    }

    /**
     *
     * @return
     */
    public Object getPayload() {
        return pay;
    }

    private final MessageProperty prop;
    private final Object pay;
}
