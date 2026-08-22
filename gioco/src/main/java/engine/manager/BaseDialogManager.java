package engine.manager;

import engine.model.BaseAtto;
import engine.model.BaseDialogo;
import engine.model.BaseScelta;

/**
 * Classe astratta che gestisce i dialogi.
 * @param <D> tipo generico per un dialogo, in questo caso BaseDialogo
 */
public abstract class BaseDialogManager<D extends BaseDialogo> extends BaseManager {
    /** Dialogo corrente. */
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

    /**
     * Metodi per interagire con il dialogo.
     * @return Dialogo corrente
     */
    public abstract BaseDialogo getDialogo();

    /**
     * Restituisce il dialogo con un certo id.
     * @param id id del dialogo da restituire
     * @return Dialogo
     */
    public abstract BaseDialogo getDialogoById(String id);

    /**
     * Sceglie l'opzione scelta dal dialogo.
     * @param scelta indice dell'opzione da scegliere
     * @return Scelta scelta
     */
    public abstract BaseScelta scegliOpzione(int scelta);

    /**
     * Metodo per far avanzare il dialogo.
     */
    public abstract void autoAvanza();

    /**
     * Avanza al prossimo dialogo.
     */
    public abstract void prossimoDialogo();

    /**
     * Restituisce l'atto corrente.
     * @return Atto corrente
     */
    public abstract BaseAtto<D> getAtto();
}
