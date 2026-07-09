package progetto.gioco.engine.manager;

import progetto.gioco.engine.model.BaseAtto;
import progetto.gioco.engine.model.BaseDialogo;
import progetto.gioco.engine.model.BaseScelta;
import progetto.gioco.game.model.Dialogo;

/**
 * Classe astratta che gestisce i dialoghi.
 */
public abstract class BaseDialogManager<D extends BaseDialogo> extends BaseManager {
    protected BaseDialogo dialogoCorrente;
    protected BaseAtto<D> atto;

    /**
     * Carica l'atto corrente.
     * @param atto atto da caricare
     */
    public abstract void setAtto(BaseAtto<Dialogo> atto);

    /**
     * Fa partire il dialogo.
     * @param idDialogo id del dialogo da far partire
     */
    public abstract void startDialogo(String idDialogo);
    public abstract BaseDialogo getDialogo();
    public abstract BaseScelta scegliOpzione(int scelta);
}
