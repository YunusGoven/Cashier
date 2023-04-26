package connector;

import gui.CarteApp;
import gui.IGui;

import java.net.InetSocketAddress;
import java.net.Socket;

public class CrefCarteConnector implements Runnable{

    private IGui carteApp;
    private String host;
    private int port;
    public CrefCarteConnector(IGui carteApp, String host, int port) {
        this.carteApp=carteApp;
        this.host = host;
        this.port = port;
    }
    @Override
    public void run() {
        int sleepTime = 50000;
        Socket socket1 = null;
        System.out.println("En attente de la présence d'une carte CREF...");
        //TODO TROUVER UNE SOLUTION
        //while (true) {
        while (socket1 == null){
            try {
                if (socket1 == null) {
                    socket1 = new Socket();
                    socket1.setSoTimeout(5000);
                    socket1.setTcpNoDelay(true);
                    socket1.connect(new InetSocketAddress(host, port));
                    System.out.println("Carte détectée !");
                    carteApp.appendOutput("CARTE DETECTE" + "\n");
                    carteApp.setBonusCard(socket1);
                }
                if (socket1.isConnected()) {
                    socket1.sendUrgentData(0);
                }
                if(!socket1.isConnected()) {
                    socket1 = new Socket();
                    socket1.setSoTimeout(5000);
                    socket1.setTcpNoDelay(true);
                    socket1.connect(new InetSocketAddress(host, port));
                    System.out.println("Carte détectée !");
                    carteApp.appendOutput("CARTE DETECTE" + "\n");
                    carteApp.setBonusCard(socket1);
                }
            } catch (Exception e) {
                socket1 = null;
                carteApp.cardDisconnected();
                try {
                    Thread.sleep(sleepTime);
                } catch (Exception ex) {}
            }
        }


    }
}
