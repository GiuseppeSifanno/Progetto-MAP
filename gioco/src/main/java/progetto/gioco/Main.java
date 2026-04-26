package progetto.gioco;

import progetto.gioco.manager.DialogManager;
import progetto.gioco.model.Atto;
import progetto.gioco.model.Dialogo;
import progetto.gioco.model.Scelta;

import java.util.*;

public class Main {
    static void main(String[] args) {

        Map<String, Dialogo> dialoghi = new HashMap<>();

        List<Scelta> scelte = new ArrayList<>();
        scelte.add(new Scelta("s1", "Sono felice", "d2"));
        scelte.add(new Scelta("s2", "Sono triste", "d3"));

        Dialogo dialogo = new Dialogo("d1", "Ciao come stai?", scelte);
        dialoghi.put(dialogo.getIdDialogo(), dialogo);

        dialogo = new Dialogo("d2", "Sono contento per te");
        dialoghi.put(dialogo.getIdDialogo(), dialogo);

        dialogo = new Dialogo("d3", "Come mai sei triste?");
        dialoghi.put(dialogo.getIdDialogo(), dialogo);

        Atto atto = new Atto("a1", dialoghi, "d1");

        DialogManager dm = new DialogManager();
        dm.caricaAtto(atto);

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
