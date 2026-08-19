import java.util.Scanner;

import game.manager.GameManager;
import game.model.Atto;
import game.model.Dialogo;
import game.model.Scelta;
import engine.model.Battuta;
import engine.model.Personaggio;

public class Main {
    static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        GameManager gameManager = new GameManager();

        gameManager.init();
        gameManager.start();

        while (gameManager.isRunning()) {
            avviaDialogo(gameManager, sc);
            gameManager.cambiaScena("a1");
            avviaDialogo(gameManager, sc);
            gameManager.cambiaScena("a2");
            avviaDialogo(gameManager, sc);

            gameManager.stop();
        }
        sc.close();
    }

    public static void avviaDialogo(GameManager gameManager, Scanner sc) {
        Dialogo dialogo;
        Atto atto = (Atto) gameManager.getDialogManager().getAtto();

        do {
            dialogo = (Dialogo) gameManager.getDialogManager().getDialogo();

            if (dialogo == null) {
                System.out.println("Fine della conversazione.");
                break;
            }

            for (Battuta b : dialogo.getBattute()) {
                if (b.personaggioId() == null || b.personaggioId().isEmpty()) {
                    System.out.println(b.testo());
                } else {
                    Personaggio p = atto.getPersonaggio(b.personaggioId());
                    String nome = (p != null) ? p.getNome() : b.personaggioId();
                    System.out.println(nome + ": " + b.testo());
                }
            }

            int i = 0;
            if (dialogo.getNumeroScelte() != 0) {
                for (Scelta s : dialogo.getScelte()) {
                    System.out.println((i + 1) + " - " + s.getTesto());
                    i++;
                }
                int n;
                do {
                    System.out.print("\nInserisci la tua scelta: ");
                    n = sc.nextInt();
                } while (n > dialogo.getNumeroScelte() || n <= 0);
                gameManager.getDialogManager().scegliOpzione(n - 1);
            }
            else {
                System.out.println("\n[Premi INVIO per continuare...]");
                sc.nextLine();

                gameManager.getDialogManager().prossimoDialogo();
            }

        } while (dialogo != null);
    }
}