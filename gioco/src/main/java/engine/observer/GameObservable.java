package engine.observer;

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
     * @param event Evento da notificare
     */
    void notifyObserver(GameObserver observer, GameEvent event);

    /**
     * Notifica tutti gli observer
     * @param event Evento da notificare
     */
    void notifyObservers(GameEvent event);
}
