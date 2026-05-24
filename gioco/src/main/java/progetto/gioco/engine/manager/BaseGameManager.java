package progetto.gioco.engine.manager;

public abstract class BaseGameManager {
    private BaseDialogManager dialogManager;
    private BaseInventarioManager inventarioManager;
    private BasePuzzleManager puzzleManager;
    private BaseSaveManager saveManager;

    public abstract void startGame();
    public abstract void cambiaScena(String id);
}
