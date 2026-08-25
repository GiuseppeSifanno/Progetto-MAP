package game.minigioco;

import engine.manager.BaseDialogManager;
import engine.manager.BaseInventarioManager;
import engine.observer.GameEvent;
import engine.observer.GameObserver;
import engine.observer.TipoEvento;
import game.manager.InventarioManager;
import game.model.minigioco.Erba;
import game.model.minigioco.ZuppaFogliantiConfig;
import game.manager.DialogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ZuppaFogliantiManager {

    private final ZuppaFogliantiConfig config;
    private final ZuppaFogliantiState state = new ZuppaFogliantiState();
    private final InventarioManager inventarioManager;
    private final DialogManager dialogManager;
    private final List<GameObserver> observers = new ArrayList<>();

    private ExecutorService indicatoreExecutor;

    public ZuppaFogliantiManager(ZuppaFogliantiConfig config,
                                    BaseInventarioManager inventarioManager,
                                    BaseDialogManager<?> dialogManager) {
        this.config = config;
        this.inventarioManager = (InventarioManager) inventarioManager;
        this.dialogManager = (DialogManager) dialogManager;
    }

    // ---------- LIFECYCLE ----------

    /** Chiamato quando il giocatore avvia il minigioco (es. da int_giungla_capo_villaggio senza flag). */
    public void avviaMinigioco() {
        state.setFaseCorrente(ZuppaFogliantiState.Fase.NAVIGATRICE);
        notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_AVVIATO, null));
        notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_FASE_CAMBIATA, ZuppaFogliantiState.Fase.NAVIGATRICE));
    }

    /** Da chiamare in reset() del GameManager, ferma il thread se attivo. */
    public void reset() {
        fermaIndicatore();
        state.reset(); // riporta tutto a IDLE / contatori a 0
    }

    // ---------- FASE NAVIGATRICE — hook GUI ----------

    /**
     * Chiamato dalla GUI quando il giocatore clicca un'erba.
     * @param idErba id dell'erba cliccata
     */
    public void onErbaSelezionata(String idErba) {
        if (state.getFaseCorrente() != ZuppaFogliantiState.Fase.NAVIGATRICE) return;

        boolean corretta = config.erbeDisponibili().stream()
                .filter(e -> e.id().equals(idErba))
                .findFirst()
                .map(Erba::commestibile)
                .orElse(false);

        if (corretta) {
            int totale = state.incrementaErbeCorrette();
            notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_ERBA_ESITO,
                    new EsitoErba(idErba, true)));
            if (totale >= config.erbeCorretteRichieste()) {
                passaAFaseCombattente();
            }
        } else {
            notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_ERBA_ESITO,
                    new EsitoErba(idErba, false)));
            // nessuna penalità hard salvo diversa decisione di design
        }
    }

    // ---------- FASE COMBATTENTE — thread + hook GUI ----------

    private void passaAFaseCombattente() {
        state.setFaseCorrente(ZuppaFogliantiState.Fase.COMBATTENTE);
        notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_FASE_CAMBIATA, ZuppaFogliantiState.Fase.COMBATTENTE));
        avviaIndicatore();
    }

    /** Avvia il thread che fa oscillare l'indicatore. Chiamato internamente, non dalla GUI. */
    private void avviaIndicatore() {
        state.setThreadAttivo(true);
        indicatoreExecutor = Executors.newSingleThreadExecutor();
        indicatoreExecutor.submit(this::loopIndicatore);
    }

    private void loopIndicatore() {
        int posizione = 0;
        int direzione = 1; // 1 = sale, -1 = scende
        try {
            while (state.isThreadAttivo() && !Thread.currentThread().isInterrupted()) {
                posizione += direzione * 2; // velocità di oscillazione, parametrizzabile
                if (posizione >= 100) { posizione = 100; direzione = -1; }
                if (posizione <= 0)   { posizione = 0;   direzione = 1;  }

                state.setPosizioneIndicatore(posizione);
                notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_INDICATORE_AGGIORNATO, posizione));

                Thread.sleep(config.velocitaIndicatoreMs());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // ripristina il flag, come da best practice
        }
    }

    private void fermaIndicatore() {
        state.setThreadAttivo(false);
        if (indicatoreExecutor != null) {
            indicatoreExecutor.shutdownNow();
            try {
                indicatoreExecutor.awaitTermination(500, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Chiamato dalla GUI quando il giocatore preme il tasto/clic per "colpire".
     * Legge la posizione corrente dell'indicatore (scritta dal thread) e valuta l'esito.
     * Thread-safe: posizioneIndicatore è un AtomicInteger, letto senza lock aggiuntivi.
     */
    public void onPressioneCombattente() {
        if (state.getFaseCorrente() != ZuppaFogliantiState.Fase.COMBATTENTE) return;

        int posizione = state.getPosizioneIndicatore();
        boolean successo = posizione >= config.zonaVerdeMin() && posizione <= config.zonaVerdeMax();

        notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_COLPO_ESITO,
                new EsitoColpo(successo, posizione)));

        if (successo) {
            int totale = state.incrementaColpiRiusciti();
            if (totale >= config.colpiRichiesti()) {
                fermaIndicatore();
                passaAFaseCapitano();
            }
        }
        // se fallito: nessun reset dei colpi già fatti, salvo diversa decisione di design
    }

    // ---------- FASE CAPITANO — hook GUI ----------

    private void passaAFaseCapitano() {
        state.setFaseCorrente(ZuppaFogliantiState.Fase.CAPITANO);
        notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_FASE_CAMBIATA, ZuppaFogliantiState.Fase.CAPITANO));
    }

    /**
     * Chiamato dalla GUI quando il giocatore usa un oggetto dall'inventario in questa fase.
     * @param idOggetto id dell'oggetto usato (ci si aspetta la tazza da tè)
     */
    public void onOggettoUsato(String idOggetto) {
        if (state.getFaseCorrente() != ZuppaFogliantiState.Fase.CAPITANO) return;
        if (!idOggetto.equals(config.idOggettoRichiestoFaseCapitano())) return;
        if (!inventarioManager.hasOggetto(idOggetto)) return;

        completaMinigioco();
    }

    // ---------- COMPLETAMENTO ----------

    private void completaMinigioco() {
        state.setFaseCorrente(ZuppaFogliantiState.Fase.COMPLETATO);
        inventarioManager.aggiungiOggettoDaId(config.idFlagCompletamento()); // flag_zuppa_pronta
        notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_COMPLETATO, config.idFlagCompletamento()));
        // eventuale AVVIA_DIALOGO di chiusura (Foglianti pacifici) va agganciato qui se serve
        // dialogManager.startDialogo("d_zuppa_completata");
    }

    // ---------- OBSERVER (stesso pattern di InterazioneObserver) ----------

    public void addObserver(GameObserver observer) {
        if (!observers.contains(observer)) observers.add(observer);
    }

    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(GameEvent event) {
        for (GameObserver o : observers) {
            o.onEvent(event);
        }
    }

    // ---------- record di supporto per i payload eventi ----------
    public record EsitoErba(String idErba, boolean corretta) {}
    public record EsitoColpo(boolean successo, int posizione) {}
}