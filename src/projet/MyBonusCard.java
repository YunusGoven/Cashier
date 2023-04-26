// APPLET
/*
package projet;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.Util;

public class MyBonusCard extends Applet {

    // D�finition des constante APDU
    final static byte MY_BONUS_CARD_CLA = (byte) 0xAB;
    final static byte INS_ADD_POINT = (byte) 0x21;
    final static byte INS_READ_POINT = (byte) 0x31;
    final static byte INS_RESET_POINT = (byte) 0x41;
    final static byte INS_SET_CARD_NUMBER = (byte) 0x51;
    final static byte INS_READ_CARD_NUMBER = (byte) 0x61;

    //D�finition des erreurs
    // Indique que le depasse le nombre maximum des points
    final static short SW_EXCEED_MAXIMUM_BALANCE = 0x6A84;
    //Indique que la carte est deja sette
    final static short SW_CARD_ALREADY_SET = 0x6A86;
    //Indique que la carte ne contient pas de num
    final static short SW_CARD_NOT_SET = 0x6A87;

    // D�finition des variables
    // Nombre de points
    private short nbPoints;
    // Numero de carte
    private byte[] numCartes;
    // Nombre maximum de point
    final static short MAX_BALANCE = 0x7FFF;

    private MyBonusCard() {
        this.nbPoints = 0;
    }

    public static void install(byte bArray[], short bOffset, byte bLength)
            throws ISOException {
        new MyBonusCard().register();
    }

    public void process(APDU apdu) throws ISOException {
        byte[] buffer = apdu.getBuffer();

        //Verifier si cest la commande de selection d'applet
        if (selectingApplet())
            return;
        //Verifier si la classe (CLA) correspond a celle de l'applet
        if (buffer[ISO7816.OFFSET_CLA] != MY_BONUS_CARD_CLA)
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);

        //Verifier quelle commande est appele
        byte ins = buffer[ISO7816.OFFSET_INS];
        switch (ins) {
            // ajouter point
            case INS_ADD_POINT:
                addPoints(apdu);
                break;
            // lire le nombre de point
            case INS_READ_POINT:
                readPoints(apdu);
                break;
            // remise a zero des points
            case INS_RESET_POINT:
                resetPoints(apdu);
                break;
            // lire num carte
            case INS_READ_CARD_NUMBER:
                readCardNumber(apdu);
                break;
            // set numero de carte
            case INS_SET_CARD_NUMBER:
                setCardNumber(apdu);
                break;
            // commande non connu
            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }

    }

    private void addPoints(APDU apdu) {
        if (this.numCartes == null) {
            ISOException.throwIt(SW_CARD_NOT_SET);
        }
        byte[] buffer = apdu.getBuffer();
        // r�cup�ration de la longueur des donn�es
        byte numBytes = buffer[ISO7816.OFFSET_LC];
        // lecture de ces donn�es
        byte byteRead = (byte) (apdu.setIncomingAndReceive());
        // a-t-on tout lu ?
        if ((numBytes != 1) || (byteRead != 1))
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
        // montant de la recharge
        byte montant = buffer[ISO7816.OFFSET_CDATA];

        if ((short) (this.nbPoints + montant) > MAX_BALANCE)
            ISOException.throwIt(SW_EXCEED_MAXIMUM_BALANCE);
        nbPoints += montant;

    }

    private void readPoints(APDU apdu) {
        if (this.numCartes == null) {
            ISOException.throwIt(SW_CARD_NOT_SET);
        }

        byte[] buffer = apdu.getBuffer();
        apdu.setOutgoing();
        apdu.setOutgoingLength((byte) 2);
        Util.setShort(buffer, (short) 0, nbPoints);
        apdu.sendBytes((short) 0, (short) 2);

    }

    private void resetPoints(APDU apdu) {
        if (this.numCartes == null) {
            ISOException.throwIt(SW_CARD_NOT_SET);
        }
        this.nbPoints = 0 ;
    }

    private void readCardNumber(APDU apdu) {
        if (this.numCartes == null) {
            ISOException.throwIt(SW_CARD_NOT_SET);
        }
        byte[] buffer = apdu.getBuffer();


        // Copier le tableau de bytes dans le buffer d'APDU
        Util.arrayCopyNonAtomic(numCartes, (short) 0, buffer, (short) 0, (short) numCartes.length);

        // Envoyer les donn�es dans le buffer d'APDU
        apdu.setOutgoing();
        apdu.setOutgoingLength((short) numCartes.length);
        apdu.sendBytes((short) 0, (short) numCartes.length);


    }

    private void setCardNumber(APDU apdu) {
        if (this.numCartes != null)
            ISOException.throwIt(SW_CARD_ALREADY_SET);
        byte[] buffer = apdu.getBuffer();
        short longueur = apdu.getIncomingLength();
        this.numCartes = new byte[longueur];
        short data = buffer[ISO7816.OFFSET_CDATA];

        Util.arrayCopyNonAtomic(buffer, (short) ISO7816.OFFSET_CDATA, numCartes, (short) 0, longueur);

    }
}

 */
