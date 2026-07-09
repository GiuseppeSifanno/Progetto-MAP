package engine.manager;

/**
 * Classe astratta per tutti i Manager
 */
public abstract class BaseManager {
    /**
     * Inizializza tutte le risorse del manager
     */
    public abstract void init();

    /**
     * Resetta tutte le risorse del manager
     */
    public abstract void reset();
}
