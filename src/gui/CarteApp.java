package gui;

import connector.*;

import javax.smartcardio.CardTerminal;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;

public class CarteApp extends JFrame implements IGui {

    private JTextField outputTextArea;
    JButton jButton;
    IAppletConnector cref ;
    IAppletConnector terminal ;

int conut = 0;
    public CarteApp() {

        jButton = new JButton("coucou");
        jButton.setBounds(50,100,95,30);
        jButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.out.println("btn");
                System.out.println(terminal.connect());
                terminal.powerUpCard();
            }
        });
        outputTextArea = new JTextField();
        outputTextArea.setBounds(50,50, 150,20);
        outputTextArea.setEditable(false);


        // Configuration de la fenêtre
        add(jButton);
        add(outputTextArea);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLayout(null);
        setVisible(true);

        //Carte
        Thread carteConnector = new Thread(new TerminalCarteConnector(this));
        carteConnector.start();
        Thread crefConnector = new Thread(new CrefCarteConnector(this, "localhost", 9025));
        crefConnector.start();
    }

    public void appendOutput(String message) {
        outputTextArea.setText(message + "\n");
    }

    public void setBonusCard(CardTerminal cardTerminal) {
        terminal = new TerminalAppletConnector((byte)0xAB,(byte)0x21,(byte)0x31,(byte) 0x41,(byte) 0x61,(byte)0x51, cardTerminal);
//                    IAppletConnector terminal = new TerminalAppletConnector((byte) 0xAB, (byte) 0x21, (byte) 0x31, (byte) 0x41, (byte) 0x61, (byte) 0x51, cardTerminal);
//                    if (terminal.connect(null, 0))
//                        if (terminal.powerUpCard()) {
//                            if (terminal.selectApplet((byte) 0x00, (byte) 0xA4, (byte) 0x04, (byte) 0x00, new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x00, 0x00})) {
//                                // TODO INTERRERACT WITH CARD
//                            }
//                        }
        //****
//        System.out.println("La carte est detecté");
//        appletConnector.connect("localhost", 90125);
//        appletConnector.powerUpCard();
//        appletConnector.selectApplet((byte) 0x00,(byte)  0xA4,(byte) 0x04, (byte) 0x00,new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,0x07, 0x08, 0x09, 0x00, 0x00 });

    }
    public void setBonusCard(Socket socket) {
        cref = new CrefAppletConnector((byte) 0xAB,(byte) 0x21,(byte) 0x31,(byte) 0x41,(byte) 0x61, (byte) 0x51, socket);

    }

    @Override
    public void cardDisconnected() {

    }


    public static void main(String[] args) {
        // Lancement de l'application Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CarteApp();
            }
        });
    }
}
