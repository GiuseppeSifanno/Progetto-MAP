package game.observer;

import engine.observer.GameEvent;
import engine.observer.GameObserver;
import engine.model.BaseDialogo;
import engine.model.BaseOggetto;
import game.model.SceltaEffettuata;
import game.model.PassoQuestCompletato;
import game.ui.GameUIListener;

public class GUIObserver implements GameObserver {
    private final GameUIListener listener;

    public GUIObserver(GameUIListener listener) {
        this.listener = listener;
    }

    @Override
    public void onEvent(GameEvent evento) {
        switch (evento.getTipo()) {
            case DIALOGO_CAMBIATO -> listener.onDialogoCambiato((BaseDialogo) evento.getPayload());
            case SCELTA_EFFETTUATA -> listener.onSceltaEffettuata((SceltaEffettuata) evento.getPayload());
            case OGGETTO_AGGIUNTO -> listener.onOggettoAggiunto((BaseOggetto) evento.getPayload());
            case OGGETTO_RIMOSSO -> listener.onOggettoRimosso((BaseOggetto) evento.getPayload());
            case ATTO_CAMBIATO -> listener.onAttoCambiato((String) evento.getPayload());
            case PUZZLE_RISOLTO -> listener.onPuzzleRisolto(String.valueOf(evento.getPayload()));
            case MESSAGGIO_MOSTRATO -> listener.onMessaggioMostrato((String) evento.getPayload());
            case QUEST_COMPLETATA -> listener.onQuestCompletata((PassoQuestCompletato) evento.getPayload());
        }
    }
}