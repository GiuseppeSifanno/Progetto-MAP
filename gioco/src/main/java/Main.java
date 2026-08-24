import game.manager.GameManager;
import game.gui.GestoreSchermate;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        GameManager gm = new GameManager();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Naufraghi all'Isola Misteriosa");

            frame.setMinimumSize(new java.awt.Dimension(1024, 768));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // apre massimizzata, con barra del titolo

            GestoreSchermate gestoreSchermate = new GestoreSchermate(frame, gm);

            gestoreSchermate.mostra(GestoreSchermate.MENU); // parte dal menu
            frame.setVisible(true);

            // Forza un primo ricalcolo delle posizioni ora che il frame ha una dimensione reale
            SwingUtilities.invokeLater(() -> {
                frame.revalidate();
                frame.repaint();
            });
        });
    }
}
