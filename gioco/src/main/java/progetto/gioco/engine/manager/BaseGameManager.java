package progetto.gioco.engine.manager;

public abstract class BaseGameManager extends BaseManager{
    protected BaseDialogManager dialogManager;
    protected BaseInventarioManager inventarioManager;
    protected BasePuzzleManager puzzleManager;
    protected BaseSaveManager saveManager;

    public abstract void startGame();
    public abstract void cambiaScena(String id);
}
