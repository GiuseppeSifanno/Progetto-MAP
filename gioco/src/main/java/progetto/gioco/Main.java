package progetto.gioco;

import progetto.gioco.loader.DialogLoader;
import progetto.gioco.manager.DialogManager;
import progetto.gioco.model.Atto;
import progetto.gioco.model.Dialogo;
import progetto.gioco.model.Scelta;

import java.util.*;

public class Main {
    static void main(String[] args) {
        DialogManager dm = new DialogManager();
        DialogLoader loader = new DialogLoader();

        Atto atto = loader.load("dialogs/atto1.json");
        dm.startDialogo(atto);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            Dialogo d = dm.getDialogo();

            if (d == null) {
                System.out.println("Fine dialogo");
                break;
            }

            System.out.println("\nNPC: " + d.getTesto());

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

            Scelta s = dm.scegliOpzione(scelta);
            System.out.println("\nTu: " + s.getTesto());
        }
    }
}
