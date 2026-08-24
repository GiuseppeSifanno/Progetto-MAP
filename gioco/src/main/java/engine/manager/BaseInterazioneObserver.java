package engine.manager;

import engine.observer.GameObservable;
import game.model.Interazione;

import java.util.List;
import java.util.Map;

/**
 * Classe astratta che gestisce gli observer delle interazioni.
 */
public abstract class BaseInterazioneObserver extends BaseManager implements GameObservable {
    /**
     * Map che contiene tutte le interazioni.
     */
    protected Map<String, Interazione> interazioni;

    /**
     * Tenta un'interazione: verifica le condizioni e applica gli effetti
     * se soddisfatte, altrimenti notifica il messaggio di blocco.
     * @param id id dell'interazione tentata
     */
    public abstract void tentaInterazione(String id);

    /**
     * Carica le zone dal file.
     * @param nomiZone nomi delle zone da caricare
     */
    public abstract void caricaZone(List<String> nomiZone);
}