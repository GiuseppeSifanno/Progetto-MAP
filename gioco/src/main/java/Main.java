import engine.observer.GameEvent;
import engine.observer.TipoEvento;
import game.manager.GameManager;
import game.gui.GestoreSchermate;
import game.model.PassoQuestCompletato;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        GameManager gm = new GameManager();
        gm.init();
        gm.start();

        // TEST: aggiungi oggetti di prova all'inventario
        gm.getInventarioManager().aggiungiOggettoDaId("o1");
        gm.getInventarioManager().aggiungiOggettoDaId("o2");
        gm.getInventarioManager().aggiungiOggettoDaId("o3");
        gm.getInventarioManager().aggiungiOggettoDaId("o3");
        gm.getInventarioManager().aggiungiOggettoDaId("o4");
        gm.getInventarioManager().aggiungiOggettoDaId("o5");
        gm.getInventarioManager().aggiungiOggettoDaId("o6");
        gm.getInventarioManager().aggiungiOggettoDaId("o7");
        gm.getInventarioManager().aggiungiOggettoDaId("o8");
        gm.getInventarioManager().aggiungiOggettoDaId("o9");
        gm.getInventarioManager().aggiungiOggettoDaId("o10");

        PassoQuestCompletato q = new PassoQuestCompletato("q1", "p1");
        gm.onEvent(new GameEvent(TipoEvento.QUEST_COMPLETATA, q));

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