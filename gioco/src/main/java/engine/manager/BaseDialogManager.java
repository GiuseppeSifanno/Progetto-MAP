package engine.manager;

import engine.model.BaseAtto;
import engine.model.BaseDialogo;
import engine.model.BaseScelta;

/**
 * Classe astratta che gestisce i dialoghi.
 */
public abstract class BaseDialogManager<D extends BaseDialogo> extends BaseManager {
    protected BaseAtto<D> atto;

    /**
     * Carica l'atto corrente.
     * @param atto atto da caricare
     */
    public abstract void setAtto(BaseAtto<D> atto);

    /**
     * Fa partire il dialogo.
     * @param idDialogo id del dialogo da far partire
     */
    public abstract void startDialogo(String idDialogo);
    public abstract BaseDialogo getDialogo();
    public abstract BaseDialogo getDialogoById(String id);
    public abstract BaseScelta scegliOpzione(int scelta);
    public abstract void autoAvanza();
    public abstract void prossimoDialogo();

    /**
     * @return Atto
     */
    public abstract BaseAtto<D> getAtto();
}
