package progetto.gioco.engine.loader;

public interface Loadable<T> {
    public T load(String path);
}
