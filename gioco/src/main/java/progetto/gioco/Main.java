package progetto.gioco;

import java.util.Scanner;

import progetto.gioco.game.manager.GameManager;
import progetto.gioco.game.model.Dialogo;
import progetto.gioco.game.model.Scelta;

public class Main {
    /** 
     * @param args
     */
    static void main(String[] args) {
        GameManager gameManager = new GameManager();

        gameManager.init();

        // Carico il primo atto
        gameManager.startGame();

        Scanner sc = new Scanner(System.in);
        
        // Stampa
        Dialogo dialogo;
        do {
            dialogo = (Dialogo) gameManager.getDialogManager().getDialogo();
            System.out.println("Testo: " + dialogo.getTesto());
            int i = 0;
            if (dialogo.getNumeroScelte() != 0){
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
                gameManager.getDialogManager().startDialogo("d4");
            }
        } while (dialogo != null);
    }
}
