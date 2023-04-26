package connector;

import gui.CarteApp;
import gui.IGui;

import javax.smartcardio.*;
import java.util.List;

public class TerminalCarteConnector implements Runnable {

    private IGui carteApp;
    public TerminalCarteConnector(IGui carteApp) {
        this.carteApp = carteApp;
    }

    private boolean isVirtual(List<CardTerminal> terminalList) {
        String javacos0 = "JAVACOS Virtual Contact Reader 0";
        String javacos1 = "JAVACOS Virtual Contactless Reader 1";
        int nbJavaCosInList = (int) terminalList.stream().filter(cardTerminal -> cardTerminal.getName().equals(javacos0) || cardTerminal.getName().equals(javacos1)).count();
        return terminalList.size() == nbJavaCosInList;
    }

    @Override
    public void run() {
            try {
                // Créer le terminal de carte
                TerminalFactory factory = TerminalFactory.getDefault();
                CardTerminals terminals = factory.terminals();

                // Attendre la présence d'une carte
                System.out.println("En attente de la présence d'une carte...");
                List<CardTerminal> terminalsList = terminals.list();
                if (terminalsList.isEmpty() || isVirtual(terminalsList)) {
                    carteApp.appendOutput("Aucun lecteur de carte trouvé");
                    System.err.println("Aucun lecteur de carte trouvé.");
                }
                CardTerminal cardTerminal = terminalsList.get(0);
                detecterCarteConnectee(cardTerminal);
            } catch (Exception e) {

            }

    }

    private void detecterCarteConnectee(CardTerminal cardTerminal) {
        while (true) {
            try {

                boolean cardPresent = cardTerminal.waitForCardPresent(0);
                Card d = cardTerminal.connect("*");
                if (cardPresent) {
                    // Une carte a été détectée
                    System.out.println("Carte détectée !");
                    carteApp.appendOutput("CARTE DETECTE" + "\n");
                    //-----------------------------------------------------


                    carteApp.setBonusCard(cardTerminal);
                    cardTerminal.waitForCardAbsent(0);
                    carteApp.appendOutput("CARTE DeDETECTE" + "\n");
                    carteApp.cardDisconnected();
                }
            } catch (Exception e) {
                System.err.println("Une erreur s'est produite lors de l'attente de la carte");
            }
        }
    }
}
