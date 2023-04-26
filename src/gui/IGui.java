package gui;

import javax.smartcardio.CardTerminal;
import java.net.Socket;

public interface IGui {
    void appendOutput(String text);
    void setBonusCard(CardTerminal cardTerminal);
    void setBonusCard(Socket socket);
    void cardDisconnected();
}
