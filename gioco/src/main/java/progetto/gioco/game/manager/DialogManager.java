package progetto.gioco.game.manager;

import java.util.Map;

import progetto.gioco.engine.manager.BaseDialogManager;
import progetto.gioco.engine.model.BaseAtto;
import progetto.gioco.engine.observer.GameEvent;
import progetto.gioco.engine.observer.GameObserver;
import progetto.gioco.engine.observer.TipoEvento;
import progetto.gioco.game.model.Dialogo;
import progetto.gioco.game.model.Scelta;

public class DialogManager extends BaseDialogManager {
    private Map<String, Dialogo> dialoghi;
    private Dialogo corrente;

    /** 
     * @param atto
     */
    @Override
    public void setAtto(BaseAtto atto) {
        this.atto = atto;

        this.dialoghi = atto.getDialoghi();
        this.corrente = atto.getDialoghi().get(atto.getDialogoIniziale());

        GameEvent event = new GameEvent();
        event.setTipo(TipoEvento.ATTO_CAMBIATO);
        event.setPayload(atto.getIdAtto());
    }

    /** 
     * @param idDialogo
     */
    @Override
    public void startDialogo(String idDialogo) {
        this.dialoghi = atto.getDialoghi();
        this.corrente = dialoghi.get(idDialogo);
    }

    /** 
     * @return Dialogo
     */
    @Override
    public Dialogo getDialogo() {
        return corrente;
    }

    /** 
     * @param index
     * @return Scelta
     */
    @Override
    public Scelta scegliOpzione(int index) {
        if (corrente.getScelte().isEmpty()) {
            corrente = null;
            return null;
        }

        Scelta scelta = corrente.getScelte().get(index);

        // Avanza al dialogo specificato dalla scelta
        String nextId = scelta.getNext();
        if (nextId.isEmpty())
            // Controlla se il dialogo corrente è terminato e ha un nextId automatico
            corrente = dialoghi.get(corrente.getNextId());
        else {
            corrente = dialoghi.get(nextId);
        }

        autoAvanza();

        return scelta;
    }

    /**
     * Se il dialogo corrente non ha scelte e ha un nextId definito,
     * avanza automaticamente al dialogo successivo.
     */
    private void autoAvanza() {
        while (corrente != null && 
            corrente.getNumeroScelte() == 0 && 
            corrente.getNextId() != null && 
            !corrente.getNextId().isEmpty()) {
            
            String nextId = corrente.getNextId();
            corrente = dialoghi.get(nextId);
        }
    }

    @Override
    public void init() {
        // I dialoghi vengono caricati quando servono
    }

    @Override
    public void reset() {
        this.dialoghi.clear();
        this.corrente = null;
        this.dialogoCorrente = null;
    }
}