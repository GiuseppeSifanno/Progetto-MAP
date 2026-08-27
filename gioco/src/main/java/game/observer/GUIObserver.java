package game.observer;

import engine.observer.GameEvent;
import engine.observer.GameObserver;
import engine.model.BaseDialogo;
import engine.model.BaseOggetto;
import game.model.SceltaEffettuata;
import game.model.PassoQuestCompletato;
import game.gui.GameUIListener;

/**
 * Observer che riceve eventi dal GameManager e li invia al listener della GUI.
 */
public class GUIObserver implements GameObserver {
    /** Listener della GUI. */
    private final GameUIListener listener;

    /**
     * Costruttore della classe.
     * @param listener Listener della GUI
     */
    public GUIObserver(GameUIListener listener) {
        this.listener = listener;
    }

    @Override
    public void onEvent(GameEvent evento) {
        switch (evento.getTipo()) {
            case DIALOGO_CAMBIATO -> {
                BaseDialogo dialogo = (BaseDialogo) evento.getPayload();
                listener.onDialogoCambiato(dialogo);
            }
            case SCELTA_EFFETTUATA  ->  listener.onSceltaEffettuata((SceltaEffettuata) evento.getPayload());
            case OGGETTO_AGGIUNTO   ->  listener.onOggettoAggiunto((BaseOggetto) evento.getPayload());
            case OGGETTO_RIMOSSO    ->  listener.onOggettoRimosso((BaseOggetto) evento.getPayload());
            case ATTO_CAMBIATO      ->  listener.onAttoCambiato((String) evento.getPayload());
            case PUZZLE_RISOLTO     ->  listener.onPuzzleRisolto(String.valueOf(evento.getPayload()));
            case MESSAGGIO_MOSTRATO ->  listener.onMessaggioMostrato((String) evento.getPayload());
            case QUEST_COMPLETATA   ->  listener.onQuestCompletata((PassoQuestCompletato) evento.getPayload());
        }
    }
}