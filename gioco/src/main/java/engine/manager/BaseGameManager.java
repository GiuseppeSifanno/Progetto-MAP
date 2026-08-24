package engine.manager;

import engine.database.DBManager;

/**
 * Classe astratta che gestisce il gioco.
 */
public abstract class BaseGameManager extends BaseManager {
    /** Manager che gestisce i dialogi. */
    protected BaseDialogManager<?> dialogManager;
    /** Manager che gestisce l'inventario. */
    protected BaseInventarioManager inventarioManager;
    /** Manager che gestisce i puzzle. */
    protected BasePuzzleManager puzzleManager;
    /** Manager che gestisce i salvataggio. */
    protected BaseSaveManager saveManager;
    /** Manager che gestisce la connessione al database. */
    protected DBManager dbManager;

    /**
     * Cambia la scena.
     * @param idAtto id dell'atto da cambiare
     */
    public abstract void cambiaScena(String idAtto);

    /**
     * Restituisce il dialog manager.
     * @return Dialog manager
     */
    public BaseDialogManager<?> getDialogManager() {
        return dialogManager;
    }

    /**
     * Restituisce il manager dell'inventario.
     * @return Inventario manager
     */
    public BaseInventarioManager getInventarioManager() {
        return inventarioManager;
    }

    /**
     * Restituisce il manager dei puzzle.
     * @return Puzzle manager
     */
    public BasePuzzleManager getPuzzleManager() {
        return puzzleManager;
    }

    /**
     * Restituisce il manager dei salvataggi.
     * @return Save manager
     */
    public BaseSaveManager getSaveManager() { return saveManager; }

}
