import game.manager.GameManager;
import game.gui.GestoreSchermate;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        GameManager gm = new GameManager();

        //NON INSERIRE GAME MANAGER ALL'INTERNO DI QUESTO BLOCCO POICHè
        //CAUSEREBBE UNA VISIONE DELLA FINESTRA DI GIOCO DOPO UN PAIO DI SECONDI
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Naufraghi all'Isola Misteriosa");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setMinimumSize(new Dimension(1024, 760));
            frame.setResizable(true);
            frame.setVisible(true);
            GestoreSchermate gestoreSchermate = new GestoreSchermate(frame, gm);
            gestoreSchermate.mostra(GestoreSchermate.MENU);

        });
        gm.init();
    }
}