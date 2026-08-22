package engine.manager;

import engine.model.BaseInterazione;
import engine.observer.GameObservable;

import java.util.Map;

/**
 * Classe astratta che gestisce gli observer delle interazioni.
 */
public abstract class BaseInterazioneObserver extends BaseManager implements GameObservable {
    /**
     * Map che contiene tutte le interazioni.
     */
    protected Map<String, ? extends BaseInterazione> interazioni;

    /**
     * Tenta un'interazione: verifica le condizioni e applica gli effetti
     * se soddisfatte, altrimenti notifica il messaggio di blocco.
     * @param id id dell'interazione tentata
     */
    public abstract void tentaInterazione(String id);
}