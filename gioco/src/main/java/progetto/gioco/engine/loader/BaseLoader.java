package progetto.gioco.engine.loader;

public abstract class BaseLoader<T> {
    public abstract T load(String path);

    protected abstract T convert(Object dto);
}
