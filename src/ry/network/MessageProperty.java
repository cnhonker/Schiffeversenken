package ry.network;

/**
 * Nachrichtentypen
 * @author cnhonker
 */
public enum MessageProperty {

    SWITCH_MAIN,
    
    SWITCH_INIT,
    
    SWITCH_FELD,
    
    SWITCH_END,
    
    USER_SHOT,

    USER_SURRENDERED,

    USER_READY,

    USER_CANCELREADY,

    USER_FINISHED,

    USER_TERMINATE,

    USER_SHIPPLACED,

    USERACTION_SHIPMOVED,

    USER_CLEAR,

    USER_UNDO,

    USER_RANDOM,

    USER_READYNEXT,

    USER_CANCELREADYNEXT,

    USER_CHAT, 

    USER_HOST, 

    USER_CONN, 

    USER_SAVE,

    USER_LOAD,

    USER_SAVE_DISABLE,

    USER_LOAD_DISABLE,

    USER_GAME_SAVE,

    USER_GAME_LOAD,

    USER_GAME_SAVE_DISABLE,

    USER_GAME_LOAD_DISABLE,

    NET_SHOTP,

    NET_SURRENDERED,

    NET_REQUEST,

    NET_RESPONSE,

    NET_RDY, 

    NET_NOTRDY, 

    NET_RDYNEXTGAME, 

    NET_NOTRDYNEXTGAME, 

    NET_BYE, 

    NET_FAIL, 

    NET_UNKNOWN,

    NET_MSG,

    NET_ERROR,

    SERVER,

    CLIENT,

    UNKNOWN,

    SAVE_REQ,

    LOAD_REQ,
    
    NET_PATH,
    
    NET_PATH_REQ
}
