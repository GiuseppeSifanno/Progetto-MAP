package progetto.gioco.engine.observer;

/**
 * Interfaccia che definisce un observable.
 */
public interface GameObservable {
    /**
     * Aggiunge un observer
     * @param observer Observer da aggiungere
     */
    public void addObserver(GameObserver observer);

    /**
     * Rimuove un observer
     * @param observer Observer da rimuovere
     */
    public void removeObserver(GameObserver observer);

    /**
     * Notifca un observer
     * @param observer Observer
     * @param evento Evento da notificare
     */
    public void notifyObserver(GameObserver observer, GameEvent evento);
}
