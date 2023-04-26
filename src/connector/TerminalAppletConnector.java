package connector;

import com.sun.javacard.apduio.CadTransportException;

import javax.smartcardio.*;
import java.io.IOException;
import java.nio.ByteBuffer;

public class TerminalAppletConnector implements IAppletConnector{
    private byte CLA_APPLET;
    private byte INS_ADD_POINT ;
    private byte INS_READ_POINT ;
    private byte INS_RESET_POINT ;
    private byte INS_READ_CARD_NUMBER;
    private byte INS_SET_CARD_NUMBER;
    private CardTerminal cardTerminal;
    private CardChannel cardChannel;
    private Card card;
    public TerminalAppletConnector(byte cla_applet, byte insAddPont, byte insReadPoint, byte insResetPoint, byte insReadCardNumber, byte insSetCardNumber, CardTerminal cardTerminal) {
        this.CLA_APPLET = cla_applet;
        this.INS_READ_POINT = insReadPoint;
        this.INS_ADD_POINT = insAddPont;
        this.INS_RESET_POINT = insResetPoint;
        this.INS_READ_CARD_NUMBER = insReadCardNumber;
        this.INS_SET_CARD_NUMBER = insSetCardNumber;
        this.cardTerminal = cardTerminal;
    }

    @Override
    public boolean connect() {
        try {
            this.card = cardTerminal.connect("*");
            this.cardChannel = card.getBasicChannel();
            return true;
        } catch (CardException e) {
            System.err.println("Unable to connect to the card");
            close();
            return false;
        }

    }

    @Override
    public void close() {
        try {
            card.disconnect(true);
        } catch (CardException ex) {
            System.err.println("Unable to disconnect the card");
        }
    }

    @Override
    public boolean powerUpCard() {
        System.out.println("PowerUp not necessary with terminal");
        return true;
    }

    private CommandAPDU setCommandApdu(byte ins, byte... data) {
        CommandAPDU commandAPDU =
                data.length == 0 ?
                        new CommandAPDU(CLA_APPLET, ins, 0x00, 0x00)
                        :
                        new CommandAPDU(CLA_APPLET, ins, 0x00, 0x00, data);

        return commandAPDU;
    }

    @Override
    public boolean selectApplet(byte cla, byte ins, byte p1, byte p2, byte[] aid) {
        try {
            CommandAPDU commandAPDU = new CommandAPDU(cla, ins, p1, p2, aid);
            ResponseAPDU responseAPDU = cardChannel.transmit(commandAPDU);
            if (responseAPDU.getSW() != 9000) {
                System.err.println("Error: SELECT applet return : "+ responseAPDU.getSW());
                close();
                return false;
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error when SELECTING the applet");
            close();
            return false;
        }
    }

    @Override
    public ResponseData getPointFromCard() {
        try {
            CommandAPDU commandAPDU = setCommandApdu(INS_READ_POINT);
            ResponseAPDU responseAPDU = cardChannel.transmit(commandAPDU);
            if (responseAPDU.getSW() == 9000)
                return  new ResponseData(responseAPDU.getSW(),  ByteBuffer.wrap(responseAPDU.getData()).getInt());
            return new ResponseData(responseAPDU.getSW(),-1);
        } catch (Exception e) {
            System.err.println("Error when READ POINT");
            return new ResponseData(-1,-1);
        }

    }

    @Override
    public ResponseData addPointToCard(int number)  {
        try {
            byte[] data = new byte[1];
            data[0] = (byte) number;
            CommandAPDU commandAPDU = setCommandApdu(INS_ADD_POINT, data);
            ResponseAPDU responseAPDU = cardChannel.transmit(commandAPDU);
            if (responseAPDU.getSW() == 9000)
                return new ResponseData(responseAPDU.getSW(),1);
            return new ResponseData(responseAPDU.getSW(),-1);
        } catch (Exception e) {
            System.err.println("Error when ADD POINT");
            return new ResponseData(-1,-1);
        }
    }

    @Override
    public ResponseData resetPointCard()  {
        try {
            CommandAPDU commandAPDU = setCommandApdu(INS_RESET_POINT);
            ResponseAPDU responseAPDU = cardChannel.transmit(commandAPDU);
            if (responseAPDU.getSW() == 9000)
                return new ResponseData(responseAPDU.getSW(),1);
            return new ResponseData(responseAPDU.getSW(),-1);
        } catch (Exception e) {
            System.err.println("Error when RESET POINT");
            return new ResponseData(-1,-1);
        }
    }

    @Override
    public ResponseData getCardNumber() {
        try {
            CommandAPDU commandAPDU = setCommandApdu(INS_READ_CARD_NUMBER);
            ResponseAPDU responseAPDU = cardChannel.transmit(commandAPDU);
            if (responseAPDU.getSW() == 9000)
                return new ResponseData(responseAPDU.getSW(),ByteBuffer.wrap(responseAPDU.getData()).getInt());
            return new ResponseData(responseAPDU.getSW(),-1);
        } catch (Exception e) {
            System.err.println("Error when READ CARD NUMBER");
            return new ResponseData(-1,-1);
        }
    }

    @Override
    public ResponseData setCardNumber(int cardNumber) {
        try {
            byte[] data = ByteBuffer.allocate(4).putInt(cardNumber).array();
            CommandAPDU commandAPDU = setCommandApdu(INS_SET_CARD_NUMBER, data);
            ResponseAPDU responseAPDU = cardChannel.transmit(commandAPDU);
            if (responseAPDU.getSW() == 9000)
                return new ResponseData(responseAPDU.getSW(),1);
            return new ResponseData(responseAPDU.getSW(),-1);
        } catch (Exception e) {
            System.err.println("Error when SET CARD NUMBER");
            return new ResponseData(-1,-1);
        }
    }

    @Override
    public boolean isConnected() {
        try {
            return cardTerminal.isCardPresent();
        } catch (Exception e) {
            return false;
        }
    }
}
