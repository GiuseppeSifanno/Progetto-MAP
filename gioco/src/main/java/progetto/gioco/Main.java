package progetto.gioco;

import progetto.gioco.game.controller.GameController;
import progetto.gioco.game.model.Atto;
import progetto.gioco.game.model.Dialogo;
import progetto.gioco.game.model.NPC;
import progetto.gioco.game.model.Scelta;
import java.util.*;

public class Main {
    /** 
     * @param args
     */
    static void main(String[] args) {
        GameController gc = new GameController();

        gc.caricaAtto("dialogs/atto1.json");

        Scanner scanner = new Scanner(System.in);
        NPC npc = new NPC("d1", "Leonardo Bossetti");
        //simulo interazione con un NPC
        gc.interagisci(npc);

        while (true) {
            Dialogo d =  gc.getDialogoCorrente();

            if (d == null) {
                System.out.println("Fine dialogo");
                break;
            }

            System.out.println("\n" + npc +": "  + d.getTesto());

            if (d.getNumeroScelte() == 0) {
                System.out.println("Fine dialogo");
                break;
            }

            System.out.println("Scelte:");
            for (int i = 0; i < d.getNumeroScelte(); i++) {
                System.out.println((i + 1) + " - " + d.getScelte().get(i).getTesto());
            }

            System.out.print("Scelta: ");
            int scelta = scanner.nextInt();

            Scelta s = gc.scegliOpzione(scelta);
            System.out.println("\nTu: " + s.getTesto());
        }
    }
}
