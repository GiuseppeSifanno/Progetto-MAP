import game.manager.GameManager;
import game.gui.GestoreSchermate;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        GameManager gm = new GameManager();
        gm.init();
        gm.start();

        // TEST: aggiungi oggetti di prova all'inventario
        gm.getInventarioManager().aggiungiOggettoDaId("o1"); // Lente
        gm.getInventarioManager().aggiungiOggettoDaId("o2"); // Bastone
        gm.getInventarioManager().aggiungiOggettoDaId("o3"); // Foglie

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Naufraghi all'Isola Misteriosa");
            frame.setMinimumSize(new java.awt.Dimension(1024, 768));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

            GestoreSchermate gestoreSchermate = new GestoreSchermate(frame, gm);
            gestoreSchermate.mostra(GestoreSchermate.MENU);
            frame.setVisible(true);
        });
    }
}