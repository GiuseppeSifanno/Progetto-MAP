package progetto.gioco.engine.loader;

public interface Loadable<T> {
    T load(String path);
}
