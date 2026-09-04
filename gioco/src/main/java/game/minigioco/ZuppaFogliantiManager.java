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

/**
 * Gestisce lo stato del minigioco Zuppa Foglianti.
 * @author Graziano
 */
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

    public void avviaMinigioco() {
        state.setFaseCorrente(ZuppaFogliantiState.Fase.NAVIGATRICE);
        notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_AVVIATO, null));
        notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_FASE_CAMBIATA, ZuppaFogliantiState.Fase.NAVIGATRICE));
    }

    public void reset() {
        fermaIndicatore();
        state.reset();
    }

    // ---------- FASE NAVIGATRICE — hook GUI ----------

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
        }
    }

    // ---------- FASE COMBATTENTE — thread + hook GUI ----------

    private void passaAFaseCombattente() {
        state.setFaseCorrente(ZuppaFogliantiState.Fase.COMBATTENTE);
        notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_FASE_CAMBIATA, ZuppaFogliantiState.Fase.COMBATTENTE));
        avviaIndicatore();
    }

    private void avviaIndicatore() {
        state.setThreadAttivo(true);
        indicatoreExecutor = Executors.newSingleThreadExecutor();
        indicatoreExecutor.submit(this::loopIndicatore);
    }

    private void loopIndicatore() {
        int posizione = 0;
        int direzione = 1;
        try {
            while (state.isThreadAttivo() && !Thread.currentThread().isInterrupted()) {
                posizione += direzione * 2;
                if (posizione >= 100) { posizione = 100; direzione = -1; }
                if (posizione <= 0)   { posizione = 0;   direzione = 1;  }

                state.setPosizioneIndicatore(posizione);
                notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_INDICATORE_AGGIORNATO, posizione));

                Thread.sleep(config.velocitaIndicatoreMs());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
    }

    // ---------- FASE CAPITANO — hook GUI ----------

    private void passaAFaseCapitano() {
        state.setFaseCorrente(ZuppaFogliantiState.Fase.CAPITANO);
        notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_FASE_CAMBIATA, ZuppaFogliantiState.Fase.CAPITANO));
    }

    public void onOggettoUsato(String idOggetto) {
        if (state.getFaseCorrente() != ZuppaFogliantiState.Fase.CAPITANO) return;
        if (!idOggetto.equals(config.idOggettoRichiestoFaseCapitano())) return;
        if (!inventarioManager.hasOggetto(idOggetto)) return;

        completaMinigioco();
    }

    // ---------- COMPLETAMENTO ----------

    private void completaMinigioco() {
        state.setFaseCorrente(ZuppaFogliantiState.Fase.COMPLETATO);
        inventarioManager.aggiungiOggettoDaId(config.idOggettoRisultato());
        notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_COMPLETATO, config.idOggettoRisultato()));
    }

    /**
     * Flusso semplificato: chiamato dal bottone "Crea zuppa" in GUI dopo la
     * raccolta erbe, SALTA le fasi Combattente/Capitano. Ferma comunque il
     * thread dell'indicatore (partito automaticamente a fine raccolta) e
     * aggiunge la zuppa (o12) all'inventario, come farebbe completaMinigioco().
     * La chiusura vera e propria dell'atto avviene tramite l'interazione
     * "int_giungla_capo_villaggio" già presente in giungla.json.
     */
    public void creaZuppa() {
        fermaIndicatore();
        state.setFaseCorrente(ZuppaFogliantiState.Fase.COMPLETATO);
        inventarioManager.aggiungiOggettoDaId(config.idOggettoRisultato());
        notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_COMPLETATO, config.idOggettoRisultato()));
    }

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

    public record EsitoErba(String idErba, boolean corretta) {}
    public record EsitoColpo(boolean successo, int posizione) {}
}