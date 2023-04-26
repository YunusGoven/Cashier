package gui;

import connector.*;

import javax.smartcardio.CardTerminal;
import javax.swing.*;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MyAppForm  implements IGui{
    private JLabel listTitle;
    private JTextField nbMB;
    private JTextField nbTV;
    private JTextField nbC;
    private JTextField nbCa;
    private JButton btnPay;
    private JButton btnGetPoint;
    private JButton btnRemiseAZeroButton;
    private JTextField tfPayer;
    private JTextField tfNbPoint;
    private JTextField tfResetPoint;
    private JTextField tfAddPoint;
    private JTextField tfStatus;
    private JTextField tfTotal;
    private JButton totalButton;
    public JPanel main;
    private JTextField cardInfo;
    private double priceMB = 5.6, priceTV = 1.5, priceCapu = 3.2, priceCaf = 2.5;
    private double total;
    private int nbPoint ;
    private int cardNumber;
    private final List<IAppletConnector> appletConnectors = new ArrayList<>();


    public MyAppForm () {

        cardNumber = 0;
        total = 0;
        nbPoint = 0;
        //Carte
        cardConnector();

        totalPressButton();
        payPressButton();
        getPointPressButton();
        resetPointPressButton();
        addPointPressButton();
    }

    private void cardConnector() {
        Thread carteConnector = new Thread(new TerminalCarteConnector(this));
        carteConnector.start();
        Thread crefConnector = new Thread(new CrefCarteConnector(this, "localhost", 9025));
        crefConnector.start();
    }

    private void totalPressButton() {
        totalButton.addActionListener(e -> {
            int mb = Integer.parseInt(nbMB.getText());
            int tv = Integer.parseInt(nbTV.getText());
            int c = Integer.parseInt(nbC.getText());
            int ca = Integer.parseInt(nbCa.getText());

            total = (mb*priceMB) + (tv*priceTV) + (c*priceCapu) + (ca*priceCaf);
            tfTotal.setText(""+total);
            if (total != 0.0) {
                tfPayer.setEditable(true);
                btnPay.setEnabled(true);
            }
        });
    }

    private void addPointPressButton() {
        appletConnectors.forEach(iAppletConnector -> {
            if(iAppletConnector.isConnected()) {
                int nbAdd = (int) (total / 10);
                ResponseData responseData = iAppletConnector.addPointToCard(nbAdd);
                if (responseData.getValue() != -1) {
                    tfNbPoint.setText(nbPoint + nbAdd +"");
                    tfAddPoint.setText(nbAdd + " point ajouté");
                } else {
                    if (responseData.getStatus() != -1) {
                        System.out.println("ADD POINT: SW "+ responseData.getStatus());
                        if (responseData.getStatus() == 0x6A84) {
                            tfNbPoint.setText("Dépasse le nombre max de point");
                        } else {
                            tfNbPoint.setText("Erreur d'ajout des points");
                        }
                    } else {
                        tfNbPoint.setText("Erreur d'ajout des points");
                    }
                }
            } else {
                cardDisconnected();
                appletConnectors.remove(iAppletConnector);
            }
        });
    }

    private void resetPointPressButton() {
        btnRemiseAZeroButton.addActionListener(e -> appletConnectors.forEach(iAppletConnector -> {
            if (total != 0.0) {
                if (iAppletConnector.isConnected()) {
                    ResponseData responseData = iAppletConnector.resetPointCard();
                    if (responseData.getValue() != -1) {
                        tfNbPoint.setText("0");
                        //todo update total
                        tfResetPoint.setText("Remis a zero");
                        total = total - (total * 0.1);
                        tfTotal.setText("" + total);

                        nbPoint = 0;
                    } else {
                        if (responseData.getStatus() != -1) {
                            System.out.println("RESET POINT: SW " + responseData.getStatus());
                            tfNbPoint.setText("Erreur de reset des points");
                        } else {
                            tfNbPoint.setText("Erreur de reset des points");
                        }
                    }
                } else {
                    cardDisconnected();
                    appletConnectors.remove(iAppletConnector);
                }
            }
        }));

    }

    private void getPointPressButton() {
        btnGetPoint.addActionListener(e -> appletConnectors.forEach(iAppletConnector -> {
            if(iAppletConnector.isConnected()) {
                ResponseData responseData = iAppletConnector.getPointFromCard();
                if (responseData.getValue() != -1) {
                    nbPoint = responseData.getValue();
                    tfNbPoint.setText(String.valueOf(responseData.getValue()));
                } else {
                    if (responseData.getStatus() != -1) {
                        System.out.println("GET POINT: SW "+ responseData.getStatus());
                        tfNbPoint.setText("Erreur de lecture des points");
                    } else {
                        tfNbPoint.setText("Erreur de lecture des points");
                    }
                }
            } else {
                cardDisconnected();
                appletConnectors.remove(iAppletConnector);
            }
        }));
    }

    private void payPressButton() {
        btnPay.addActionListener(e -> {
            if(total!=0.0) {
                if (!tfPayer.getText().isBlank()) {
                    double montant = Double.parseDouble(tfPayer.getText());
                    if (montant < total) {
                        tfStatus.setText("Montant insuffisant");
                    } else {
                        double restant = montant - total;
                        addPointPressButton();
                        DecimalFormat dc = new DecimalFormat("0.00");
                        total = 0.0;
                        tfStatus.setText("A rendre: "+ dc.format(restant));
                        tfPayer.setEditable(false);
                        btnPay.setEnabled(false);
                        tfTotal.setText("0");
                        tfPayer.setText("");
                        nbC.setText("0");
                        nbCa.setText("0");
                        nbTV.setText("0");
                        nbMB.setText("0");
                        tfResetPoint.setText("");
                    }
                } else {
                    tfStatus.setText("Montant insuffisant");
                }
            }
        });
    }

    @Override
    public void appendOutput(String text) {

    }

    @Override
    public void setBonusCard(CardTerminal cardTerminal) {
        nbPoint = 0;
        appletConnectors.add(new TerminalAppletConnector((byte) 0xAB, (byte) 0x21, (byte) 0x31, (byte) 0x41, (byte) 0x61, (byte) 0x51, cardTerminal));
        cardInfo.setText("Carte connecté");
        appletConnectors.forEach(iAppletConnector -> {
            if (iAppletConnector.connect()) {
                if (iAppletConnector.powerUpCard()) {
                    if (iAppletConnector.selectApplet((byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x00, 0x00})) {
                        ResponseData responseData = iAppletConnector.getCardNumber();
                        if (responseData.getValue() != -1) {
                            cardNumber = responseData.getValue();
                            cardInfo.setText(cardInfo.getText()+ ", num: "+cardNumber );
                        } else {
                            if (responseData.getStatus() != -1) {
                                System.out.println("GET CardNumber: SW " + responseData.getStatus());
                                if (responseData.getStatus() == 0x6A87) {
                                    //todo
                                    int num = ThreadLocalRandom.current().nextInt();
                                    ResponseData responseData1 = iAppletConnector.setCardNumber(num);
                                    if (responseData1.getValue() != -1) {
                                        cardNumber = num;
                                        cardInfo.setText("Card initialisier, num carte : " + num);
                                    } else {
                                        if (responseData1.getStatus() != -1) {
                                            System.out.println("GET CardNumber: SW " + responseData.getStatus());
                                        }
                                    }
                                }
                            } else {
                                cardInfo.setText("Erreur de lecture de la carte");
                            }
                        }
                    } else {
                        cardInfo.setText("Erreur de lecture de la carte");
                        cardDisconnected();
                        appletConnectors.remove(iAppletConnector);
                    }
                } else {
                    cardInfo.setText("Erreur de lecture de la carte");
                    cardDisconnected();
                    appletConnectors.remove(iAppletConnector);
                }
            } else {
                cardInfo.setText("Erreur de lecture de la carte");
                cardDisconnected();
                appletConnectors.remove(iAppletConnector);
            }
        });
        if (cardNumber != 0) {
            btnGetPoint.setEnabled(true);
            btnRemiseAZeroButton.setEnabled(true);
        }
    }

    @Override
    public void setBonusCard(Socket socket) {
        nbPoint=0;
        appletConnectors.add(new CrefAppletConnector((byte) 0xAB,(byte) 0x21,(byte) 0x31,(byte) 0x41,(byte) 0x61, (byte) 0x51, socket));
        cardInfo.setText("Carte connecté");

        appletConnectors.forEach(iAppletConnector -> {
            if(iAppletConnector.connect()) {
                if (iAppletConnector.powerUpCard()) {
                    if (iAppletConnector.selectApplet((byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x00, 0x00})){
                        ResponseData responseData = iAppletConnector.getCardNumber();
                        if (responseData.getValue() != -1) {
                            cardNumber = responseData.getValue();
                            cardInfo.setText(cardInfo.getText()+ ", num: "+cardNumber );
                        } else {
                            if (responseData.getStatus() != -1) {
                                System.out.println("GET CardNumber: SW " + responseData.getStatus());
                                if (responseData.getStatus() == 0x6A87) {
                                    int num = 152484256; //todo
                                    ResponseData responseData1 = iAppletConnector.setCardNumber(num);
                                    if (responseData1.getStatus() == 0x9000) {
                                        cardNumber = num;
                                        cardInfo.setText("Card initialisier, num carte : " + num);
                                    } else {
                                        if (responseData1.getValue() != -1) {
                                            System.out.println("GET CardNumber: SW " + responseData.getStatus());
                                        }
                                    }

                                } else {
                                    cardInfo.setText("Erreur de lecture de la carte");
                                }
                            }
                        }
                    } else {
                        cardInfo.setText("Erreur de lecture de la carte");
                        cardDisconnected();
                        appletConnectors.remove(iAppletConnector);
                    }
                } else {
                    cardInfo.setText("Erreur de lecture de la carte");
                    cardDisconnected();
                    appletConnectors.remove(iAppletConnector);
                }
            } else {
                cardInfo.setText("Erreur de lecture de la carte");
                cardDisconnected();
                appletConnectors.remove(iAppletConnector);
            }
        });
        if (cardNumber !=0 ) {
            btnGetPoint.setEnabled(true);
            btnRemiseAZeroButton.setEnabled(true);
        }
    }

    @Override
    public void cardDisconnected() {
        nbPoint=0;
        cardNumber = 0;
        cardInfo.setText("La carte est déconnecté");
        btnGetPoint.setEnabled(false);
        btnRemiseAZeroButton.setEnabled(false);
        cardNumber = 0;
    }
}
