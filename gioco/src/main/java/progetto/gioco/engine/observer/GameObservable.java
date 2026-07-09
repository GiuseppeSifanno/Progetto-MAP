package progetto.gioco.engine.observer;

/**
 * Interfaccia che definisce un observable.
 */
public interface GameObservable {
    /**
     * Aggiunge un observer
     * @param observer Observer da aggiungere
     */
    void addObserver(GameObserver observer);

    /**
     * Rimuove un observer
     * @param observer Observer da rimuovere
     */
    void removeObserver(GameObserver observer);

    /**
     * Notifca un observer
     * @param observer Observer
     * @param evento Evento da notificare
     */
    void notifyObserver(GameObserver observer, GameEvent evento);
}
