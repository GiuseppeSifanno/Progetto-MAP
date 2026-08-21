package engine.loader;

public interface Loadable<T> {
    T load(String path);
}
