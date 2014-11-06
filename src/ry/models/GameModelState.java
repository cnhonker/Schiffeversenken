package ry.models;

import ry.view.CardProperty;

/**
 * Aktulle Status des Models als Objekt
 * @author cnhonker
 */
public class GameModelState {
    
    public String getRoll() {
        return roll == null ? "" : roll;
    }

    public void setRoll(boolean isHost) {
        roll = isHost ? "SERVER" : "CLIENT";
    }

    public Matrix getUserMatrix() {
        return allyMatrix;
    }

    public void setMyMatrix(Matrix matrix) {
        allyMatrix = matrix;
    }

    public Matrix getMatrixOfTheEnemy() {
        return enemyMatrix;
    }

    public void setMatrixOfTheEnemy(Matrix matrix) {
        enemyMatrix = matrix;
    }

    public Matrix getEnemyMatrix() {
        return filledEnemyMatrix;
    }

    public void setCurrentMatrixEnemy(Matrix matrix) {
        filledEnemyMatrix = matrix;
    }

    public CardProperty getViewProp() {
        return view;
    }

    public void setView(CardProperty prop) {
        view = prop;
    }

    public String getMessage() {
        return chat;
    }

    public void setMessage(String msg) {
        chat = msg;
    }

    public String getUnknowErr() {
        return unknownErr;
    }

    public void setUnknownErr(String msg) {
        unknownErr = msg;
    }

    public boolean isUserTurn() {
        return isUserTurn;
    }

    public void setUserTurn(boolean userTurn) {
        isUserTurn = userTurn;
    }

    public boolean enemyReady() {
        return isEnemyRdy;
    }

    public void setEnemyRdy(boolean b) {
        isEnemyRdy = b;
    }

    public boolean istNextRoundRdy() {
        return nextRoundRdy;
    }

    public void setNextRoundRdy(boolean b) {
        nextRoundRdy = b;
    }

    public boolean isEnemyNGRdy() {
        return enemyRdyNextRound;
    }

    public void setEnemyNGRdy(boolean b) {
        enemyRdyNextRound = b;
    }

    public boolean isBattleshipRdy() {
        return battleshipRdy;
    }

    public void setBattleshipRdy(boolean b) {
        battleshipRdy = b;
    }

    public boolean isCruiserRdy() {
        return cruisersRdy;
    }

    public void setCruiserRdy(boolean b) {
        cruisersRdy = b;
    }

    public boolean isDestroyerRdy() {
        return destroyersRdy;
    }

    public void setDestroyerRdy(boolean b) {
        destroyersRdy = b;
    }

    public boolean isUbootRdy() {
        return ubootRdy;
    }

    public void setUbootRdy(boolean b) {
        ubootRdy = b;
    }

    public boolean isFieldRdy() {
        return allFilled;
    }

    public void setFieldRdy(boolean b) {
        allFilled = b;
    }

    public boolean hasLayoutShips() {
        return stillempty;
    }

    public void setLayoutShips(boolean b) {
        stillempty = b;
    }

    public boolean shipsReady() {
        return shipValid;
    }

    public void setShipsRdy(boolean b) {
        shipValid = b;
    }

    public boolean userReady() {
        return rdy;
    }

    public void setReady(boolean b) {
        rdy = b;
    }

    public ResultProperty getGameProp() {
        return prop;
    }

    public void setGameProp(ResultProperty b) {
        prop = b;
    }

    public boolean isDisconnected() {
        return enemyDiscon;
    }

    public void setEnemyNetStatus(boolean b) {
        enemyDiscon = b;
    }

    public boolean unableToConnect() {
        return unableToConnect;
    }

    public void setNetNAStatus(boolean b) {
        unableToConnect = b;
    }

    public boolean isHostNA() {
        return hostAvailable;
    }

    public void setHostStatus(boolean b) {
        hostAvailable = b;
    }

    private boolean battleshipRdy = false;
    private boolean destroyersRdy = false;
    private boolean shipValid = true;
    private boolean allFilled = false;
    private boolean cruisersRdy = false;
    private boolean stillempty = true;
    private boolean ubootRdy = false;
    private boolean isEnemyRdy;
    private boolean hostAvailable;
    private boolean unableToConnect;
    private boolean enemyRdyNextRound;
    private boolean isUserTurn;
    private boolean enemyDiscon;
    private boolean rdy;
    private boolean nextRoundRdy;
    private ResultProperty prop;
    private Matrix enemyMatrix;
    private Matrix filledEnemyMatrix;
    private Matrix allyMatrix;
    private String chat;
    private String unknownErr;
    private CardProperty view;
    private String roll;
}
