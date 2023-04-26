package connector;


import com.sun.javacard.apduio.Apdu;
import com.sun.javacard.apduio.CadT1Client;
import com.sun.javacard.apduio.CadTransportException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class CrefAppletConnector implements IAppletConnector{

    private byte CLA_APPLET;
    private byte INS_ADD_POINT ;
    private byte INS_READ_POINT ;
    private byte INS_RESET_POINT ;
    private byte INS_READ_CARD_NUMBER;
    private byte INS_SET_CARD_NUMBER;
    private Apdu apdu;
    private Socket socket;
    private CadT1Client cad;
    private BufferedInputStream input;
    private BufferedOutputStream output;
    public CrefAppletConnector(byte cla_applet, byte insAddPont, byte insReadPoint, byte insResetPoint, byte insReadCardNumber, byte insSetCardNumber, Socket socket) {
        this.CLA_APPLET = cla_applet;
        this.INS_READ_POINT = insReadPoint;
        this.INS_ADD_POINT = insAddPont;
        this.INS_RESET_POINT = insResetPoint;
        this.INS_READ_CARD_NUMBER = insReadCardNumber;
        this.INS_SET_CARD_NUMBER = insSetCardNumber;
        this.socket = socket;
    }

    @Override
    public boolean connect() {
        try {
            this.input = new BufferedInputStream(this.socket.getInputStream());
            this.output = new BufferedOutputStream(this.socket.getOutputStream());
            this.cad = new CadT1Client(this.input, this.output);
            return  true;
        } catch (Exception e) {
            System.err.println("Unable to connect to the card");
            close();
            return false;
        }
    }
    @Override
    public void close() {
        try {
            socket.close();
            cad.close();
            input.close();
            output.close();
        } catch (Exception e){

        }
    }
    @Override
    public boolean powerUpCard() {
        try {
            this.cad.powerUp();
            return true;
        } catch (Exception e) {
            System.err.println("Error when sending PowerUp command");
            close();
            return false;
        }
    }

    @Override
    public boolean selectApplet(byte cla, byte ins, byte p1, byte p2, byte[] aid) {
        try {
            this.apdu = new Apdu();
            this.apdu.command[Apdu.CLA] = cla;
            this.apdu.command[Apdu.INS] = ins;
            this.apdu.command[Apdu.P1] = p1;
            this.apdu.command[Apdu.P2] = p2;
            apdu.setDataIn(aid);
            this.cad.exchangeApdu(this.apdu);
            if (this.apdu.getStatus() != 0x9000) {
                System.err.println("Error: SELECT applet return : "+ apdu.getStatus());
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

    private void setApdu(byte ins) {
        this.apdu = new Apdu();
        this.apdu.command[Apdu.CLA] = CLA_APPLET;
        this.apdu.command[Apdu.P1] = 0x00;
        this.apdu.command[Apdu.P2] = 0x00;
        this.apdu.setLe(0x7F);
        this.apdu.command[Apdu.INS] = ins;
    }

    @Override
    public ResponseData getPointFromCard() {
        try {
            setApdu(INS_READ_POINT);
            this.cad.exchangeApdu(this.apdu);

            if (this.apdu.getStatus() == 0x9000) {
                return new ResponseData(apdu.getStatus(), ByteBuffer.wrap(apdu.dataOut).getShort());
            }
            return new ResponseData(apdu.getStatus(),-1);
        } catch (Exception e) {
            System.err.println("Error when READ POINT");
            return new ResponseData(-1,-1);
        }
    }

    @Override
    public ResponseData addPointToCard(int number) {
        try {
            setApdu(INS_ADD_POINT);
            byte[] data = new byte[1];
            data[0] = (byte) number;
            this.apdu.setDataIn(data);
            this.cad.exchangeApdu(this.apdu);
            if (this.apdu.getStatus() == 0x9000)
                return new ResponseData(apdu.getStatus(),1);
            return new ResponseData(apdu.getStatus(),-1);
        } catch (Exception e) {
            System.err.println("Error when ADD POINT");
            return new ResponseData(-1,-1);
        }
    }

    @Override
    public ResponseData resetPointCard() {
        try {
            setApdu(INS_RESET_POINT);
            this.cad.exchangeApdu(this.apdu);
            if (this.apdu.getStatus() == 0x9000)
                return new ResponseData(apdu.getStatus(),1);
            return new ResponseData(apdu.getStatus(),-1);
        } catch (Exception e) {
            System.err.println("Error when RESET POINT");
            return new ResponseData(-1,-1);
        }
    }

    @Override
    public ResponseData getCardNumber() {
        try {
            setApdu(INS_READ_CARD_NUMBER);
            this.cad.exchangeApdu(this.apdu);
            if (this.apdu.getStatus() == 0x9000)
                return new ResponseData(apdu.getStatus(),ByteBuffer.wrap(apdu.dataOut).getInt());
            return new ResponseData(apdu.getStatus(),-1);
        } catch (Exception e) {
            System.err.println("Error when READ CARD NUMBER");
            return new ResponseData(-1,-1);
        }
    }

    @Override
    public ResponseData setCardNumber(int cardNumber) {
        try {
            setApdu(INS_SET_CARD_NUMBER);
            byte[] data = ByteBuffer.allocate(4).putInt(cardNumber).array();
            this.apdu.setDataIn(data);
            this.cad.exchangeApdu(this.apdu);
            if (this.apdu.getStatus() == 0x9000)
                return new ResponseData(apdu.getStatus(),1);
            return new ResponseData(apdu.getStatus(),-1);
        } catch (Exception e) {
            System.err.println("Error when SET CARD NUMBER");
            return new ResponseData(-1,-1);
        }
    }

    @Override
    public boolean isConnected() {
        try {
            socket.sendUrgentData(0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
