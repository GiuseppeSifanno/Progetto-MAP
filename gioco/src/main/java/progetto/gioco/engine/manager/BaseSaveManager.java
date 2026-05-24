package progetto.gioco.engine.manager;

public abstract class BaseSaveManager {
    public abstract void salva(Object stato);

    public abstract Object carica(int idSlot);

    public abstract void elimina(int idSlot);
}
