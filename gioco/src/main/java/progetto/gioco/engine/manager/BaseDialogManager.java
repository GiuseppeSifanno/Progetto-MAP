package progetto.gioco.engine.manager;

import progetto.gioco.engine.model.BaseAtto;
import progetto.gioco.engine.model.BaseDialogo;
import progetto.gioco.engine.model.BaseScelta;

/**
 * Classe astratta che gestisce i dialoghi.
 */
public abstract class BaseDialogManager extends BaseManager {
    protected BaseDialogo dialogoCorrente;
    protected BaseAtto atto;

    /**
     * Carica l'atto corrente.
     * @param atto atto da caricare
     */
    public abstract void setAtto(BaseAtto atto);

    /**
     * Fa partire il dialogo.
     * @param idDialogo id del dialogo da far partire
     */
    public abstract void startDialogo(String idDialogo);

    public abstract BaseDialogo getDialogo();

    public abstract BaseScelta scegliOpzione(int scelta);
}
