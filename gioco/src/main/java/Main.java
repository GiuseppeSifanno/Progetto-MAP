import engine.observer.GameEvent;
import engine.observer.TipoEvento;
import game.manager.GameManager;
import game.gui.GestoreSchermate;
import game.model.PassoQuestCompletato;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        GameManager gm = new GameManager();
        gm.init();
        //NON INSERIRE GAME MANAGER ALL'INTERNO DI QUESTO BLOCCA POICHè
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

        //AGGIUNTI DOPO SOLO PER TEST
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
    }
}