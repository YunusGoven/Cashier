package connector;


public interface IAppletConnector {

    /**
     * Connect the card from terminal or emulator
     * @return true if successfully connect to the card or false
     */
    public boolean connect();

    /**
     * Close the connection with the card
     */
    public void close();

    /**
     * Send POWERUP command to emulator
     * @return true if there are no error else false
     */
    public boolean powerUpCard();

    /**
     * SELECT Applet method
     * @param cla class byte CLA
     * @param ins instruction byte INS
     * @param p1 parameter 1 byte P1
     * @param p2 parameter 2  byte P2
     * @param aid applet aid in array byte[] AID
     * @return true if applet was successfully select else fale
     */
    public boolean selectApplet(byte cla, byte ins, byte p1, byte p2, byte[] aid);

    /**
     * GET User POINT present in the card
     * @return RESPONSE DATA (see response data doc)
     */
    public ResponseData getPointFromCard() ;

    /**
     * ADD POINT to the user card
     * @param number number of point to add
     * @return RESPONSE DATA (see response data doc)
     */
    public ResponseData addPointToCard(int number) ;

    /**
     * RESET ALL POINT to 0 in the card INT
     * @return RESPONSE DATA (see response data doc)
     */
    public ResponseData resetPointCard() ;

    /**
     * GET CARD NUMBER of the user card
     * @return RESPONSE DATA (see response data doc)
     */
    public ResponseData getCardNumber() ;

    /**
     * SET CARD NUMBER of the user card
     * @param cardNumber card number INT
     * @return RESPONSE DATA (see response data doc)
     */
    public ResponseData setCardNumber(int cardNumber);

    /**
     *  Know if card is already connected
     * @return true or false
     */
    public boolean isConnected();
}
