package progetto.gioco.engine.observer;

public interface GameObservable {
    public void addObserver(GameObserver observer);
    public void removeObserver(GameObserver observer);
    public void notifyObserver(GameObserver observer);
}
