package engine.manager;

import engine.model.BaseInterazione;
import engine.observer.GameObservable;

import java.util.Map;

public abstract class BaseInterazioneObserver extends BaseManager implements GameObservable {
    protected Map<String, ? extends BaseInterazione> interazioni;

    /**
     * Tenta un'interazione: verifica le condizioni e applica gli effetti
     * se soddisfatte, altrimenti notifica il messaggio di blocco.
     * @param id id dell'interazione tentata
     */
    public abstract void tentaInterazione(String id);
}