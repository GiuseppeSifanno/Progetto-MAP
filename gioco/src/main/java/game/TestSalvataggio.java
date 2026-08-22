package game;

import game.manager.GameManager;
import game.model.Puzzle;
import game.model.StatoGioco;

import java.sql.SQLException;

public class TestSalvataggio {
    public static void main(String[] args) throws SQLException {
        GameManager gm = new GameManager();
        gm.init();
        gm.start(); // carica a0, imposta gameState su dialogo iniziale

        System.out.println("--- Stato PRIMA del salvataggio ---");
        stampaStato(gm.getGameState());

        // --- Inventario ---
        gm.getInventarioManager().aggiungiOggettoDaId("o1");
        gm.getInventarioManager().aggiungiOggettoDaId("o2");

        // --- Scelte effettuate: serve un atto con scelte reali, a0/a1 non ne hanno ---
        gm.cambiaScena("a2");
        gm.getDialogManager().scegliOpzione(0); // sceglie "Andiamo a investigare immediatamente" -> d2

        // --- Puzzle risolto ---
        Puzzle puzzleTest = new Puzzle("p01", "risposta");
        gm.getPuzzleManager().aggiungiPuzzle(puzzleTest);
        gm.getPuzzleManager().caricaPuzzle("p01");
        System.out.println("PUZZLE CARICATO: " + gm.getPuzzleManager().caricaPuzzle("p01").getId());
        gm.getPuzzleManager().tentaRisoluzione("risposta"); // notifica PUZZLE_RISOLTO

        gm.getPuzzleManager().tentaRisoluzione("risposta");
        System.out.println("DEBUG puzzle risolti in gameState PRIMA del salvataggio: " + gm.getGameState().getPuzzleRisolti());

        System.out.println("\n--- Salvataggio slot 1 ---");
        gm.salvaPartita(1);
        System.out.println("Salvato.");

        // simulo un riavvio: nuova istanza, stato completamente vuoto
        GameManager gm2 = new GameManager();
        gm2.init();

        System.out.println("\n--- Caricamento slot 1 su nuova istanza ---");
        gm2.caricaPartita(1);
        stampaStato(gm2.getGameState());

        System.out.println("\n--- Verifica hasOggetto su InventarioManager ---");
        System.out.println("o1 presente: " + gm2.getInventarioManager().hasOggetto("o1"));
        System.out.println("o2 presente: " + gm2.getInventarioManager().hasOggetto("o2"));

        System.out.println("\n--- Verifica dialogo ripreso ---");
        System.out.println("Dialogo in DialogManager: " + gm2.getDialogManager().getDialogo().getId());
        System.out.println("idDialogoCorrente in gameState: " + gm2.getGameState().getIdDialogoCorrente());

        System.out.println("\n--- Verifica scelte effettuate ---");
        gm2.getGameState().getScelteEffettuate().forEach(s ->
                System.out.println("dialogo=" + s.idDialogo() + " scelta=" + s.idScelta()));

        System.out.println("\n--- Verifica puzzle risolti ---");
        System.out.println("puzzle risolti nel gameState: " + gm2.getGameState().getPuzzleRisolti());
    }

    private static void stampaStato(StatoGioco stato) {
        System.out.println("Atto corrente: " + stato.getIdAttoCorrente());
        System.out.println("Dialogo corrente: " + stato.getIdDialogoCorrente());
        System.out.println("Inventario: " + stato.getInventario().oggetti().size() + " oggetti");
        System.out.println("Puzzle risolti: " + stato.getPuzzleRisolti().size());
        System.out.println("Scelte effettuate: " + stato.getScelteEffettuate().size());
    }
}