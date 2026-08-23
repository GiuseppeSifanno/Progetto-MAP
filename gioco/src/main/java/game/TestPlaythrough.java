package game;

import engine.model.BaseOggetto;
import engine.model.Battuta;
import engine.model.Personaggio;
import game.manager.GameManager;
import game.model.Atto;
import game.model.Dialogo;
import game.model.Interazione;
import game.model.Scelta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Playthrough manuale del gioco pensato per il debug dei contenuti.
 *
 * Il test non cerca di "giocare da solo": mostra chiaramente cosa sta
 * succedendo e permette di eseguire dialoghi, interazioni, flag e avanzamenti
 * di atto senza dover conoscere a memoria gli ID presenti nei JSON.
 */
public class TestPlaythrough {
    private static final String SEPARATOR = "============================================================";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        GameManager gm = new GameManager();

        gm.init();
        gm.start();

        //TODO aggiungere caricamento o nuova partita
        stampaTitolo();
        stampaStato(gm);

        while (gm.isRunning()) {
            stampaDialogoCorrente(gm, sc);

            if (!gm.isRunning()) break;

            String comando = leggiComando(sc, gm);
            //TODO aggiungere salvataggio
            if (comando.equals("q")) break;

            eseguiComando(comando, gm, sc);
        }

        System.out.println("\n" + SEPARATOR);
        System.out.println("PLAYTHROUGH TERMINATO");
        System.out.println(SEPARATOR);
        sc.close();
    }

    private static void stampaTitolo() {
        System.out.println("\n" + SEPARATOR);
        System.out.println("                 PROGETTO-MAP");
        System.out.println("              TEST PLAYTHROUGH");
        System.out.println(SEPARATOR);
        System.out.println("Questo programma serve per provare la storia e le interazioni.");
        System.out.println("Non e' necessario conoscere gli ID: vengono mostrati nel menu.\n");
    }

    private static void stampaStato(GameManager gm) {
        Atto atto = (Atto) gm.getDialogManager().getAtto();
        String idAtto = atto == null ? "?" : atto.getId();
        String idDialogo = gm.getGameState().getIdDialogoCorrente();

        System.out.println("\n" + SEPARATOR);
        System.out.println("STATO CORRENTE");
        System.out.println(SEPARATOR);
        System.out.println("Atto:    " + idAtto);
        System.out.println("Dialogo: " + (idDialogo == null ? "-" : idDialogo));
        stampaInventario(gm);
        System.out.println(SEPARATOR);
    }

    private static void stampaDialogoCorrente(GameManager gm, Scanner sc) {
        Atto atto = (Atto) gm.getDialogManager().getAtto();
        Dialogo dialogo = (Dialogo) gm.getDialogManager().getDialogo();

        if (atto == null || dialogo == null) {
            return;
        }

        System.out.println("\n" + SEPARATOR);
        System.out.println("ATTO " + atto.getId().toUpperCase() + "  |  DIALOGO " + dialogo.getId());
        System.out.println(SEPARATOR);

        for (Battuta battuta : dialogo.getBattute()) {
            Personaggio personaggio = battuta.personaggioId() == null
                    ? null
                    : atto.getPersonaggio(battuta.personaggioId());

            String nome = personaggio != null
                    ? personaggio.getNome().trim()
                    : battuta.personaggioId();

            if (nome == null || nome.isBlank()) {
                System.out.println("  " + battuta.testo().trim());
            } else {
                System.out.println("  " + nome + ": " + battuta.testo().trim());
            }
        }

        if (dialogo.getNumeroScelte() > 0) {
            System.out.println("\nSCELTE DISPONIBILI:");
            for (int i = 0; i < dialogo.getScelte().size(); i++) {
                Scelta scelta = dialogo.getScelte().get(i);
                String destinazione = scelta.getNext().isBlank()
                        ? dialogo.getNextId()
                        : scelta.getNext();
                System.out.printf("  [%d] %s  ->  %s%n", i + 1, scelta.getTesto().trim(),
                        destinazione.isBlank() ? "FINE" : destinazione);
            }
        } else if (dialogo.getNextId() != null && !dialogo.getNextId().isBlank()) {
            System.out.println("\n[INVIO] Continua -> " + dialogo.getNextId());
        } else {
            System.out.println("\n[FINE DIALOGO] Nessun dialogo successivo automatico.");
        }
    }

    private static String leggiComando(Scanner sc, GameManager gm) {
        System.out.println("\nAZIONI:");
        System.out.println("  [INVIO]      continua il dialogo corrente");
        System.out.println("  [i]          mostra le interazioni della zona");
        System.out.println("  [numero]     esegue direttamente un'interazione dal menu");
        System.out.println("  [f]          imposta un flag/minigioco completato");
        System.out.println("  [inv]        mostra l'inventario");
        System.out.println("  [stato]      mostra lo stato completo del test");
        System.out.println("  [a]          forza il passaggio all'atto successivo");
        System.out.println("  [q]          esce dal playthrough");
        System.out.print("\nComando: ");
        return sc.nextLine().trim().toLowerCase();
    }

    private static void eseguiComando(String comando, GameManager gm, Scanner sc) {
        if (comando.isBlank()) {
            continuaDialogo(gm);
            return;
        }

        switch (comando) {
            case "i", "interazioni" -> scegliInterazione(gm, sc);
            case "f", "flag" -> impostaFlag(gm, sc);
            case "inv", "inventario" -> stampaInventario(gm);
            case "stato" -> stampaStato(gm);
            case "a", "atto" -> avanzaAtto(gm);
            default -> {
                try {
                    int numero = Integer.parseInt(comando);
                    eseguiInterazionePerNumero(gm, numero);
                } catch (NumberFormatException e) {
                    System.out.println("Comando non riconosciuto. Usa [i], [f], [inv], [stato], [a] o [q].");
                }
            }
        }
    }

    private static void continuaDialogo(GameManager gm) {
        Dialogo dialogo = (Dialogo) gm.getDialogManager().getDialogo();
        if (dialogo == null) {
            System.out.println("Non c'e' un dialogo attivo.");
            return;
        }

        if (dialogo.getNumeroScelte() > 0) {
            System.out.println("Questo dialogo richiede una scelta: usa il numero della scelta mostrata sopra.");
            return;
        }

        gm.getDialogManager().prossimoDialogo();
    }

    private static void scegliInterazione(GameManager gm, Scanner sc) {
        List<Interazione> interazioni = listaInterazioni(gm);
        if (interazioni.isEmpty()) {
            System.out.println("\nNessuna interazione disponibile in questo atto.");
            return;
        }

        System.out.println("\nINTERAZIONI DELLA ZONA:");
        System.out.println("------------------------------------------------------------");
        for (int i = 0; i < interazioni.size(); i++) {
            stampaInterazioneSintetica(i + 1, interazioni.get(i), gm);
        }

        System.out.print("\nNumero interazione (INVIO per annullare): ");
        String input = sc.nextLine().trim();
        if (input.isBlank()) return;

        try {
            eseguiInterazionePerNumero(gm, Integer.parseInt(input));
        } catch (NumberFormatException e) {
            System.out.println("Numero non valido.");
        }
    }

    private static void eseguiInterazionePerNumero(GameManager gm, int numero) {
        List<Interazione> interazioni = listaInterazioni(gm);
        if (numero < 1 || numero > interazioni.size()) {
            System.out.println("Interazione inesistente.");
            return;
        }

        Interazione interazione = interazioni.get(numero - 1);
        boolean sbloccata = interazione.getCondizioni().stream()
                .allMatch(gm.getGameState().getInventario()::hasOggetto);

        System.out.println("\n> INTERAZIONE: " + interazione.getId());
        if (!interazione.getCondizioni().isEmpty()) {
            System.out.println("  Richiede: " + String.join(", ", interazione.getCondizioni()));
        } else {
            System.out.println("  Richiede: nulla");
        }

        if (!sbloccata) {
            System.out.println("  RISULTATO: BLOCCATA");
            System.out.println("  " + interazione.getMessaggioBloccato());
            return;
        }

        String attoPrima = ((Atto) gm.getDialogManager().getAtto()).getId();
        gm.getInterazioneObserver().tentaInterazione(interazione.getId());
        String attoDopo = ((Atto) gm.getDialogManager().getAtto()).getId();

        System.out.println("  RISULTATO: ESEGUITA");
        System.out.println("  " + interazione.getMessaggioSbloccato());
        stampaEffetti(interazione);

        if (!attoPrima.equals(attoDopo)) {
            System.out.println("\n>>> CAMBIO ATTO AUTOMATICO: " + attoPrima + " -> " + attoDopo + " <<<");
        }
    }

    private static void stampaInterazioneSintetica(int numero, Interazione i, GameManager gm) {
        boolean disponibile = i.getCondizioni().stream()
                .allMatch(gm.getGameState().getInventario()::hasOggetto);
        String stato = disponibile ? "SBLOCCATA" : "BLOCCATA";

        System.out.printf("  [%d] %-32s [%s]%n", numero, i.getId(), stato);
        if (!i.getCondizioni().isEmpty()) {
            System.out.println("      Richiede: " + String.join(", ", i.getCondizioni()));
        }
        System.out.println("      -> " + (disponibile ? i.getMessaggioSbloccato() : i.getMessaggioBloccato()));
    }

    private static void stampaEffetti(Interazione interazione) {
        if (interazione.getEffetti().isEmpty()) {
            System.out.println("  Effetti: nessuno");
            return;
        }

        System.out.println("  Effetti:");
        interazione.getEffetti().forEach(e ->
                System.out.println("    - " + e.tipo() + (e.valore() == null || e.valore().isBlank()
                        ? ""
                        : " -> " + e.valore()))
        );
    }

    private static void impostaFlag(GameManager gm, Scanner sc) {
        System.out.println("\nFLAG/MINIGIOCHI:");
        System.out.println("Inserisci l'ID del flag da aggiungere all'inventario tecnico.");
        System.out.println("Esempi presenti nei contenuti: flag_zuppa_pronta, flag_tunnel_illuminato, flag_montacarichi_riparato.");
        System.out.print("Flag (INVIO per annullare): ");
        String flag = sc.nextLine().trim();
        if (flag.isBlank()) return;

        gm.impostaFlag(flag);
        System.out.println("Flag impostato: " + flag);
    }

    private static void avanzaAtto(GameManager gm) {
        Atto prima = (Atto) gm.getDialogManager().getAtto();
        if (!gm.prossimoAtto()) {
            System.out.println("\nNon ci sono altri atti: fine della storia.");
            return;
        }

        Atto dopo = (Atto) gm.getDialogManager().getAtto();
        System.out.println("\n>>> PASSAGGIO MANUALE: " + prima.getId() + " -> " + dopo.getId() + " <<<");
    }

    private static List<Interazione> listaInterazioni(GameManager gm) {
        return new ArrayList<>(gm.getInterazioneObserver().getInterazioni().values()).stream()
                .sorted(Comparator.comparing(Interazione::getId))
                .toList();
    }

    private static void stampaInventario(GameManager gm) {
        List<BaseOggetto> oggetti = gm.getGameState().getInventario().oggetti();
        System.out.println("\nINVENTARIO:");
        if (oggetti.isEmpty()) {
            System.out.println("  (vuoto)");
            return;
        }

        for (BaseOggetto oggetto : oggetti) {
            System.out.println("  - " + oggetto.getId() + " : " + oggetto.getNome());
        }
    }
}
