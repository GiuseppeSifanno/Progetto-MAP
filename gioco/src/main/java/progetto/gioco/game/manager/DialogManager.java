package progetto.gioco.game.manager;

import java.util.Map;

import progetto.gioco.engine.manager.BaseDialogManager;
import progetto.gioco.engine.model.BaseAtto;
import progetto.gioco.engine.observer.GameEvent;
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

        GameEvent event = new GameEvent();
        event.setTipo(TipoEvento.ATTO_CAMBIATO);
        event.setPayload(atto.getIdAtto());  
    }

    /** 
     * @param atto
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

        corrente = dialoghi.get(scelta.getNext());

        return scelta;
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'init'");
    }

    @Override
    public void reset() {
        this.dialoghi.clear();
        this.corrente = null;
        this.dialogoCorrente = null;
    }
}