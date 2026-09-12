package game.minigioco;

import engine.observer.GameEvent;
import engine.observer.GameObservable;
import engine.observer.GameObserver;
import engine.observer.TipoEvento;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Logica del minigioco Montacarichi. Nessuna dipendenza da Swing: comunica
 * solo tramite eventi (Observer), lasciando ogni aspetto visivo a
 * {@link game.gui.MontacarichiPanel}.
 */
public class MontacarichiManager implements GameObservable {

    public enum Fase { COMBATTENTE, NAVIGATRICE }

    public record EsitoColpo(boolean successo, int colpiRiusciti, int colpiRichiesti) {}
    public record EsitoNodo(int indice, boolean corretto) {}

    private static final int COLPI_RICHIESTI = 3;
    private static final int ZONA_VERDE_MIN = 40;
    private static final int ZONA_VERDE_MAX = 60;
    private static final int INDICATORE_MAX = 100;
    private static final int PASSO_INDICATORE = 3;
    private static final long TICK_INDICATORE_MS = 30;

    /** Numero di nodi della fase Navigatrice: logica, non estetica. */
    public static final int NUMERO_NODI = 3;

    /** Id dell'interazione "sintetica" da tentare al completamento (stesso pattern della zuppa). */
    public static final String ID_INTERAZIONE_COMPLETAMENTO = "int_miniera_montacarichi";

    private final List<GameObserver> observers = new ArrayList<>();

    private Timer timerIndicatore;
    private int posizioneIndicatore = 0;
    private int direzioneIndicatore = 1;

    private int colpiRiusciti = 0;
    private int prossimoNodoAtteso = 0;
    private Fase faseCorrente;

    public void avviaMinigioco() {
        colpiRiusciti = 0;
        prossimoNodoAtteso = 0;
        faseCorrente = Fase.COMBATTENTE;

        notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_FASE_CAMBIATA, faseCorrente));

        avviaIndicatore();
    }

    private void avviaIndicatore() {
        posizioneIndicatore = 0;
        direzioneIndicatore = 1;
        fermaIndicatore();

        // Thread dedicato (criterio 5), non Swing Timer: la logica non deve
        // dipendere dall'EDT. Chi ascolta l'evento in GUI deve fare
        // l'update tramite SwingUtilities.invokeLater.
        timerIndicatore = new Timer("indicatore-montacarichi", true);
        timerIndicatore.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                aggiornaPosizioneIndicatore();
                notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_INDICATORE_AGGIORNATO, posizioneIndicatore));
            }
        }, 0, TICK_INDICATORE_MS);
    }

    private void aggiornaPosizioneIndicatore() {
        posizioneIndicatore += direzioneIndicatore * PASSO_INDICATORE;
        if (posizioneIndicatore >= INDICATORE_MAX) {
            posizioneIndicatore = INDICATORE_MAX;
            direzioneIndicatore = -1;
        } else if (posizioneIndicatore <= 0) {
            posizioneIndicatore = 0;
            direzioneIndicatore = 1;
        }
    }

    private void fermaIndicatore() {
        if (timerIndicatore != null) {
            timerIndicatore.cancel();
            timerIndicatore = null;
        }
    }

    /** Chiamato dal Panel quando il giocatore preme "COLPISCI". */
    public void onColpisci() {
        if (faseCorrente != Fase.COMBATTENTE) return;

        boolean successo = posizioneIndicatore >= ZONA_VERDE_MIN
                && posizioneIndicatore <= ZONA_VERDE_MAX;
        if (successo) colpiRiusciti++;

        notifyObservers(new GameEvent(
                TipoEvento.MINIGIOCO_COLPO_ESITO,
                new EsitoColpo(successo, colpiRiusciti, COLPI_RICHIESTI)
        ));

        if (successo && colpiRiusciti >= COLPI_RICHIESTI) {
            fermaIndicatore();
            avviaFaseNavigatrice();
        }
    }

    private void avviaFaseNavigatrice() {
        faseCorrente = Fase.NAVIGATRICE;
        prossimoNodoAtteso = 0;
        notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_FASE_CAMBIATA, faseCorrente));
    }

    /** Chiamato dal Panel quando il giocatore clicca il nodo con questo indice (0-based). */
    public void onNodoCliccato(int indice) {
        if (faseCorrente != Fase.NAVIGATRICE) return;

        boolean corretto = indice == prossimoNodoAtteso;
        notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_NODO_ESITO, new EsitoNodo(indice, corretto)));

        if (corretto) {
            prossimoNodoAtteso++;
            if (prossimoNodoAtteso >= NUMERO_NODI) {
                completaMinigioco();
            }
        }
    }

    private void completaMinigioco() {
        notifyObservers(new GameEvent(TipoEvento.MINIGIOCO_COMPLETATO, ID_INTERAZIONE_COMPLETAMENTO));
    }

    /** Valori esposti SOLO per il disegno della barra: il Panel non li duplica come costanti proprie. */
    public int getZonaVerdeMin() { return ZONA_VERDE_MIN; }
    public int getZonaVerdeMax() { return ZONA_VERDE_MAX; }
    public int getIndicatoreMax() { return INDICATORE_MAX; }

    public void reset() {
        fermaIndicatore();
        colpiRiusciti = 0;
        prossimoNodoAtteso = 0;
        posizioneIndicatore = 0;
        direzioneIndicatore = 1;
        faseCorrente = null;
    }

    @Override
    public void addObserver(GameObserver observer) {
        if (!observers.contains(observer)) observers.add(observer);
    }

    @Override
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObserver(GameObserver observer, GameEvent evento) {
        observer.onEvent(evento);
    }

    public void notifyObservers(GameEvent event) {
        for (GameObserver o : observers) {
            o.onEvent(event);
        }
    }
}