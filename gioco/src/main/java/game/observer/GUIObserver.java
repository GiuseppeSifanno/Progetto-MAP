package game.observer;

import engine.observer.GameEvent;
import engine.observer.GameObserver;
import engine.model.BaseDialogo;
import engine.model.BaseOggetto;
import game.minigioco.MontacarichiManager;
import game.minigioco.ZuppaFogliantiManager;
import game.model.SceltaEffettuata;
import game.model.PassoQuestCompletato;
import game.gui.GameUIListener;

/**
 * Observer che comunica gli eventi al GameUIListener.
 */
public class GUIObserver implements GameObserver {
    private final GameUIListener listener;

    public GUIObserver(GameUIListener listener) {
        this.listener = listener;
    }

    @Override
    public void onEvent(GameEvent evento) {
        switch (evento.getTipo()) {
            case DIALOGO_CAMBIATO   -> listener.onDialogoCambiato((BaseDialogo) evento.getPayload());
            case SCELTA_EFFETTUATA  -> listener.onSceltaEffettuata((SceltaEffettuata) evento.getPayload());
            case OGGETTO_AGGIUNTO   -> listener.onOggettoAggiunto((BaseOggetto) evento.getPayload());
            case OGGETTO_RIMOSSO    -> listener.onOggettoRimosso((BaseOggetto) evento.getPayload());
            case ATTO_CAMBIATO      -> listener.onAttoCambiato((String) evento.getPayload());
            case MESSAGGIO_MOSTRATO -> listener.onMessaggioMostrato((String) evento.getPayload());
            case QUEST_COMPLETATA   -> listener.onQuestCompletata((PassoQuestCompletato) evento.getPayload());

            // Zuppa
            case MINIGIOCO_AVVIATO -> listener.onMinigiocoAvviato();
            case MINIGIOCO_ERBA_ESITO ->
                    listener.onMinigiocoErbaEsito((ZuppaFogliantiManager.EsitoErba) evento.getPayload());

            // Montacarichi
            case MINIGIOCO_FASE_CAMBIATA ->
                    listener.onMinigiocoFaseCambiataMontacarichi((MontacarichiManager.Fase) evento.getPayload());
            case MINIGIOCO_INDICATORE_AGGIORNATO ->
                    listener.onMinigiocoIndicatoreAggiornato((Integer) evento.getPayload());
            case MINIGIOCO_COLPO_ESITO ->
                    listener.onMinigiocoColpoEsito((MontacarichiManager.EsitoColpo) evento.getPayload());
            case MINIGIOCO_NODO_ESITO ->
                    listener.onMinigiocoNodoEsito((MontacarichiManager.EsitoNodo) evento.getPayload());

            // Comune
            case MINIGIOCO_COMPLETATO -> listener.onMinigiocoCompletato((String) evento.getPayload());

            default -> { }
        }
    }
}