package engine.manager;

import engine.database.DBManager;

/**
 * Classe astratta che gestisce tutti i manager del gioco.
 */
public abstract class BaseGameManager extends BaseManager {
    protected BaseDialogManager<?> dialogManager;
    protected BaseInventarioManager inventarioManager;
    protected BasePuzzleManager puzzleManager;
    protected BaseSaveManager saveManager;
    protected DBManager dbManager;

    /**
     * Cambia la scena.
     * @param idAtto id dell'atto da cambiare
     */
    public abstract void cambiaScena(String idAtto);

    public BaseDialogManager<?> getDialogManager() {
        return dialogManager;
    }

    public BaseInventarioManager getInventarioManager() {
        return inventarioManager;
    }

    public BasePuzzleManager getPuzzleManager() {
        return puzzleManager;
    }

    public BaseSaveManager getSaveManager() { return saveManager; }

    public DBManager getDbManager() { return dbManager; }
}
