import java.util.Scanner;

import game.manager.GameManager;
import game.model.Dialogo;
import game.model.Scelta;

public class Main {

    static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        GameManager gameManager = new GameManager();

        gameManager.init();

        // Carico il primo atto
        gameManager.start();

        while (gameManager.isRunning()) {
            avviaDialogo(gameManager, sc);

            // Solo per testing, cambiamo al secondo atto
            gameManager.cambiaScena("atto2");

            avviaDialogo(gameManager, sc);

            //simulo un comando esci dal gioco
            gameManager.stop();
        }
        sc.close();
    }

    public static void avviaDialogo(GameManager gameManager, Scanner sc) {
        // Stampa
        Dialogo dialogo;
        do {
            dialogo = (Dialogo) gameManager.getDialogManager().getDialogo();

            // Se il dialogo è null, significa che è terminato
            if (dialogo == null) {
                System.out.println("Fine della conversazione.");
                break;
            }

            System.out.println("Testo: " + dialogo.getTesto());
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
            } else {
                // Dialogo senza scelte - il flusso automatico è gestito in DialogManager
                System.out.println("\n[Prosegui...]");
                break;
            }
        } while (dialogo != null);

    }
}