package progetto.gioco.engine.manager;

/**
 * Classe astratta che gestisce i salvataggi.
 */
public abstract class BaseSaveManager extends BaseManager {
    /**
     * Salva un oggetto in un slot del salvataggi.
     * @param stato Oggetto da salvare
     */
    public abstract void salva(Object stato);

    /**
     * Carica un oggetto dal salvataggi.
     * @param idSlot numero slot del salvataggio
     * @return Oggetto
     */
    public abstract Object carica(int idSlot);

    /**
     * Elimina un salvataggio.
     * @param idSlot numero slot del salvataggio
     */
    public abstract void elimina(int idSlot);
}
